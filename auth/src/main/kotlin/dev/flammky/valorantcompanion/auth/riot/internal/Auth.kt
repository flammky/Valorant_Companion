package dev.flammky.valorantcompanion.auth.riot.internal

import dev.flammky.valorantcompanion.auth.ex.AuthFailureException
import dev.flammky.valorantcompanion.auth.ex.InvalidSessionException
import dev.flammky.valorantcompanion.auth.ex.ResponseParsingException
import dev.flammky.valorantcompanion.auth.ex.UnexpectedResponseException
import dev.flammky.valorantcompanion.auth.ext.jsonObjectOrNull
import dev.flammky.valorantcompanion.auth.ext.jsonPrimitiveOrNull
import dev.flammky.valorantcompanion.auth.riot.AuthHttpRequestResponse
import dev.flammky.valorantcompanion.auth.riot.AuthRequestResponseData
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

internal suspend fun retrieveAccessToken(
    httpClient: HttpClient,
    username: String,
    password: String,
    session: AuthRequestSessionImpl
) {
    val httpRequest = HttpRequestBuilder()
        .apply {
            method = HttpMethod.Put
            url("https://auth.riotgames.com/api/v1/authorization")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
            setBody(
                body = buildJsonObject {
                    put("type", JsonPrimitive("auth"))
                    put("username", JsonPrimitive(username))
                    put("password", JsonPrimitive(password))
                    // TODO
                    put("remember", JsonPrimitive(false))
                    put("language", JsonPrimitive("en_US"))
                }
            )
        }

    val httpResponse = runCatching {
        httpClient.request(httpRequest)
    }.onFailure {
        session.onException(it as Exception)
        return
    }.getOrThrow()

    val element = runCatching {
        val str = httpResponse.bodyAsText()
        Json.decodeFromString<JsonElement>(str)
    }.onFailure { ex ->
        session.onException(ex as Exception)
        return
    }.getOrThrow()

    session.onResponse(
        AuthHttpRequestResponse(
            httpResponse.status.value,
            element
        )
    )

    val obj = element.jsonObjectOrNull
        ?: run {
            session.onException(
                UnexpectedResponseException("expected JSON object, but got ${element::class::simpleName} instead")
            )
            return
        }

    if (obj["error"]?.jsonPrimitive?.toString() == "\"auth_failure\"") {
        session.onException(
            AuthFailureException("auth_failure")
        )
        return
    }

    if (obj["error"]?.jsonPrimitive?.toString() == "\"invalid_session_id\"") {
        session.onException(
            InvalidSessionException("invalid_session_id")
        )
        return
    }

    val access_token = obj["response"]
        ?.jsonObjectOrNull?.get("parameters")
        ?.jsonObjectOrNull?.get("uri")
        ?.jsonPrimitiveOrNull
        ?.takeIf { it.isString }
        ?.toString()?.run {
            indexOf("access_token=").takeIf { it >= 0 }?.let { i ->
                drop(i + "access_token=".length).takeWhile { it != '&' }
            }
        }

    if (access_token == null || access_token.isEmpty()) {
        session.onException(
            ResponseParsingException("access_token not found")
        )
        return
    }

    val id_token = obj["response"]
        ?.jsonObjectOrNull?.get("parameters")
        ?.jsonObjectOrNull?.get("uri")
        ?.jsonPrimitiveOrNull
        ?.takeIf { it.isString }
        ?.toString()?.run {
            indexOf("id_token=").takeIf { it >= 0 }?.let { i ->
                drop(i + "id_token=".length).takeWhile { it != '&' }
            }
        }

    if (id_token == null || id_token.isEmpty()) {
        session.onException(
            ResponseParsingException("id_token not found")
        )
        return
    }

    session.parsedData(
        AuthRequestResponseData(access_token, id_token)
    )
}