package dev.flammky.valorantcompanion.pvp.ingame.internal

import dev.flammky.valorantcompanion.auth.riot.RiotAuthService
import dev.flammky.valorantcompanion.auth.riot.RiotGeoRepository
import dev.flammky.valorantcompanion.pvp.ex.UnexpectedResponseException
import dev.flammky.valorantcompanion.pvp.http.HttpClient
import dev.flammky.valorantcompanion.pvp.http.JsonHttpRequest
import dev.flammky.valorantcompanion.pvp.ingame.InGameFetchRequestResult
import dev.flammky.valorantcompanion.pvp.ingame.InGameUserClient
import dev.flammky.valorantcompanion.pvp.ingame.InGameUserMatchClient
import dev.flammky.valorantcompanion.pvp.ingame.ex.InGameMatchNotFoundException
import dev.flammky.valorantcompanion.pvp.ingame.onFailure
import dev.flammky.valorantcompanion.pvp.match.ex.UnknownTeamIdException
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

internal class RealInGameUserClient(
    private val puuid: String,
    private val auth: RiotAuthService,
    private val geo: RiotGeoRepository,
    private val httpClientFactory: () -> HttpClient
) : InGameUserClient {

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val httpClient = httpClientFactory.invoke()

    override fun fetchUserCurrentMatchIDAsync(): Deferred<InGameFetchRequestResult<String>> {
        val def = CompletableDeferred<InGameFetchRequestResult<String>>()

        val task = coroutineScope.launch(Dispatchers.IO) {
            def.complete(
                fetchUserCurrentMatchID()
                    .onFailure { exception, errorCode ->
                        exception.printStackTrace()
                    }
            )
        }.apply {
            invokeOnCompletion { ex ->
                ex?.let { def.completeExceptionally(ex) }
                check(def.isCompleted)
            }
        }

        def.invokeOnCompletion { ex ->
            ex?.let { task.cancel() }
        }


        return def
    }

    private suspend fun fetchUserCurrentMatchID(): InGameFetchRequestResult<String> {
        return InGameFetchRequestResult.buildCatching {
            val access_token = auth.get_authorization(puuid).getOrElse {
                throw IllegalStateException("Unable to retrieve access token", it)
            }.access_token
            val entitlement_token = auth.get_entitlement_token(puuid).getOrElse {
                throw IllegalStateException("Unable to retrieve entitlement token", it)
            }
            val geo = geo.getGeoShardInfo(puuid)
                ?: error("Unable to retrieve GeoShard info")

            val response = httpClient.jsonRequest(
                JsonHttpRequest(
                    method = "GET",
                    url = "https://glz-${geo.region.assignedUrlName}-1.${geo.shard.assignedUrlName}" +
                            ".a.pvp.net/core-game/v1/players/$puuid",
                    headers = listOf(
                        "X-Riot-Entitlements-JWT" to entitlement_token,
                        "Authorization" to "Bearer $access_token"
                    ),
                    body = null
                )
            )

            when(response.statusCode) {
                200 -> return success(parseUserCurrentMatchIdFromResponse(response.body))
                404 -> {
                    if (
                        response.body
                            .expectJsonObject("PlayerPreGameMatchInfo response")
                            .expectJsonProperty("errorCode")
                            .expectJsonPrimitive("errorCode")
                            .expectNotJsonNull("errorCode")
                            .content
                            .also { expectNonBlankJsonString("errorCode", it) }
                            .equals("RESOURCE_NOT_FOUND")
                    ) {
                        throw InGameMatchNotFoundException()
                    }
                }
            }

            unexpectedResponseError("Unable to GET user current match ID (${response.statusCode})")
        }
    }

    private fun parseUserCurrentMatchIdFromResponse(
        body: JsonElement
    ): String {
        val obj = body.expectJsonObject("GET UserCurrentMatchID response body")

        run {
            val prop = "Subject"
            obj
                .expectJsonProperty(prop)
                .expectJsonPrimitive(prop)
                .expectNotJsonNull(prop)
                .content
                .also { expectNonBlankJsonString(prop, it) }
                .takeIf { it == puuid }
                ?: unexpectedResponseError("Subject mismatch")
        }


        return run {
            val prop = "MatchID"
            obj
                .expectJsonProperty(prop)
                .expectJsonPrimitive(prop)
                .expectNotJsonNull(prop)
                .content
                .also { expectNonBlankJsonString(prop, it) }
        }
    }

    override fun createMatchClient(matchId: String): InGameUserMatchClient {
        return RealInGameUserMatchClient(
            puuid = puuid,
            matchID = matchId,
            httpClient = httpClient,
            auth = auth,
            geo = geo,
            disposeHttpClient = false
        )
    }

    override fun dispose() {
        httpClient.dispose()
    }

    private fun unexpectedResponseError(msg: String): Nothing {
        throw UnexpectedResponseException(msg)
    }

    private fun unexpectedTeamIdError(msg: String): Nothing {
        throw UnknownTeamIdException(msg)
    }

    private fun unexpectedJsonElementError(
        propertyName: String,
        expectedElement: KClass<out JsonElement>,
        got: JsonElement
    ): Nothing = unexpectedResponseError(
        "expected $propertyName to be ${expectedElement.simpleName} " +
                "but got ${got::class.simpleName} instead"
    )

    private fun unexpectedJsonArrayElementError(
        arrayName: String,
        expectedElement: KClass<out JsonElement>,
        got: JsonElement
    ): Nothing = unexpectedResponseError(
        "expected element of $arrayName to be ${expectedElement.simpleName} " +
                "but got ${got::class.simpleName} instead"
    )

    private fun unexpectedJsonValueError(
        propertyName: String,
        message: String
    ): Nothing = unexpectedResponseError(
        "value of $propertyName was unexpected, message: $message"
    )

    private fun missingJsonPropertyError(
        propertyName: String
    ): Nothing = unexpectedResponseError(
        "$propertyName property not found"
    )

    private fun JsonElement.expectJsonPrimitive(
        propertyName: String
    ): JsonPrimitive {
        return this as? JsonPrimitive
            ?: unexpectedJsonElementError(propertyName, JsonPrimitive::class, this)
    }

    private fun JsonElement.expectJsonObject(
        propertyName: String
    ): JsonObject {
        return this as? JsonObject
            ?: unexpectedJsonElementError(propertyName, JsonObject::class, this)
    }

    private fun JsonElement.expectJsonArray(
        propertyName: String
    ): JsonArray {
        return this as? JsonArray
            ?: unexpectedJsonElementError(propertyName, JsonArray::class, this)
    }

    private fun JsonObject.expectJsonProperty(
        propertyName: String
    ): JsonElement {
        return get(propertyName)
            ?: missingJsonPropertyError(propertyName)
    }

    private fun JsonElement.expectJsonObjectAsJsonArrayElement(
        arrayName: String
    ): JsonObject {
        return this as? JsonObject
            ?: unexpectedJsonArrayElementError(arrayName, JsonObject::class, this)
    }

    private fun JsonPrimitive.expectNotJsonNull(
        propertyName: String
    ): JsonPrimitive {
        if (this is JsonNull) unexpectedJsonValueError(
            propertyName,
            "value is JsonNull"
        )
        return this
    }

    private inline fun JsonElement.ifJsonNull(
        block: () -> Unit
    ): JsonElement {
        if (this is JsonNull) block()
        return this
    }

    private fun JsonElement.jsonNullable(): JsonElement? = if (this is JsonNull) null else this

    private fun expectNonBlankJsonString(
        propertyName: String,
        content: String
    ) {
        if (content.isBlank()) unexpectedJsonValueError(
            propertyName,
            "value is blank"
        )
    }

    private fun expectJsonNumber(
        propertyName: String,
        content: String
    ) {
        if (content.isBlank()) unexpectedJsonValueError(
            propertyName,
            "expected JsonNumber but value is blank"
        )
        if (content.any { !it.isDigit() }) unexpectedJsonValueError(
            propertyName,
            "expected JsonNumber but value contains non digit char"
        )
    }

    private fun expectJsonBoolean(
        propertyName: String,
        content: String
    ) {
        when {
            content.isBlank() -> unexpectedJsonValueError(
                propertyName,
                "expected JsonBoolean but value is blank"
            )
            content.any { it.isDigit() } -> unexpectedJsonValueError(
                propertyName,
                "expected JsonNumber but value contains digit char"
            )
            content != "true" && content != "false" -> unexpectedJsonValueError(
                propertyName,
                "expected JsonBoolean but value is not a boolean"
            )
        }
    }
}