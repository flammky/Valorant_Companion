package dev.flammky.valorantcompanion.auth.riot.internal

import dev.flammky.valorantcompanion.auth.ex.BadAuthorizationParamException
import dev.flammky.valorantcompanion.auth.ex.CredentialExpiredException
import dev.flammky.valorantcompanion.auth.ex.ResponseParsingException
import dev.flammky.valorantcompanion.auth.ex.UnexpectedResponseException
import dev.flammky.valorantcompanion.auth.ext.jsonObjectOrNull
import dev.flammky.valorantcompanion.auth.ext.jsonPrimitiveOrNull
import dev.flammky.valorantcompanion.auth.riot.EntitlementHttpRequestResponse
import dev.flammky.valorantcompanion.auth.riot.EntitlementRequestResponseData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

internal suspend fun retrieveEntitlementToken(
    httpClient: HttpClient,
    access_token: String,
    session: EntitlementRequestSessionImpl
) {
    val httpRequest = HttpRequestBuilder()
        .apply {
            method = HttpMethod.Post
            url("https://entitlements.auth.riotgames.com/api/token/v1")
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $access_token")
            }
        }

    val httpResponse = runCatching {
        httpClient.request(httpRequest)
    }.getOrElse {
        session.onException(it as Exception)
        return
    }

    val str = runCatching {
        httpResponse.body<String>()
    }.getOrElse {
        session.onException(it as Exception)
        return
    }

    val obj = runCatching {
        val element = Json.decodeFromString<JsonElement>(str)
        element.jsonObjectOrNull
            ?: run {
                throw UnexpectedResponseException("expected JSON object, but got ${element::class::simpleName} instead")
            }
    }.getOrElse {
        session.onException(it as Exception)
        return
    }

    session.onResponse(
        EntitlementHttpRequestResponse(httpResponse.status.value, obj)
    )

    when(val code = obj["errorCode"]?.jsonPrimitive?.toString()) {
        null, "" -> Unit
        "\"BAD_AUTHORIZATION_PARAM\"" -> {
            val msg = obj["message"]?.jsonPrimitive?.toString()
            val ex = BadAuthorizationParamException(msg)
            session.onException(ex)
            return
        }
        "\"CREDENTIALS_EXPIRED\"" -> {
            val msg = obj["message"]?.jsonPrimitive?.toString()
            val ex = CredentialExpiredException(msg)
            session.onException(ex)
            return
        }
        else -> {
            session.onException(ResponseParsingException("Unknown Credential Error Code: $code"))
            return
        }
    }

    val entitlementToken = obj["entitlements_token"]
        ?.jsonPrimitiveOrNull
        ?.takeIf { it.isString }
        ?.toString()
        ?.removeSurrounding("\"")

    if (entitlementToken.isNullOrBlank()) {
        session.onException(
            ResponseParsingException("entitlements_token not found")
        )
        return
    }

    session.onParse(EntitlementRequestResponseData(entitlementToken))
}

internal suspend fun retrieveReAuthEntitlementToken(
    httpClient: HttpClient,
    session: RiotJointReauthorizeSessionImpl
) {
    val access_token = session._reAuthAccessToken
        ?: run {
            return
        }
    val httpRequest = HttpRequestBuilder()
        .apply {
            method = HttpMethod.Post
            url("https://entitlements.auth.riotgames.com/api/token/v1")
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer $access_token")
            }
        }

    val httpResponse = runCatching {
        httpClient.request(httpRequest)
    }.getOrElse {
        session.retrieveEntitlementTokenRequestError("Unexpected Error while executing entitlement token request")
        return
    }

    if (httpResponse.status.value !in 200..299) {
        session.retrieveEntitlementUnexpectedHttpStatusCode(
            httpStatusCode = httpResponse.status.value
        )
        return
    }

    val body = runCatching {
        httpResponse
            .bodyAsChannel()
            .toByteArray()
    }.getOrElse {
        session.retrieveEntitlementTokenBodyResponseError(
            msg = "Unexpected Error while consuming entitlement response body packets"
        )
        return
    }

    val str = runCatching {
        String(body)
    }.getOrElse {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "Unexpected Error while deserializing entitlement response body to Java String (UTF-8)"
        )
        return
    }

    val element = runCatching {
        Json.decodeFromString<JsonElement>(str)
    }.getOrElse {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "Unexpected Error while deserializing entitlement response body, body is not a JSON"
        )
        return
    }

    val obj = element.jsonObjectOrNull
        ?: run {
            session.retrieveAuthAccessTokenUnexpectedResponseBody(
                "Expected entitlement response body to be a Json Object, but got ${element::class::simpleName} instead"
            )
            return
        }

    runCatching {
        when(val code = obj["errorCode"]?.jsonPrimitiveOrNull?.content) {
            null, "" -> Unit
            "BAD_AUTHORIZATION_PARAM" -> {
                val msg = obj["message"]?.jsonPrimitiveOrNull?.toString()
                val ex = BadAuthorizationParamException(msg)
                session.retrieveEntitlementAuthDenied("BAD_AUTHORIZATION_PARAM")
                return
            }
            "CREDENTIALS_EXPIRED" -> {
                val msg = obj["message"]?.jsonPrimitiveOrNull?.toString()
                val ex = CredentialExpiredException(msg)
                session.retrieveEntitlementAuthDenied("CREDENTIALS_EXPIRED")
                return
            }
            else -> {
                session.retrieveEntitlementAuthDenied("UNKNOWN_ERROR_CODE")
                return
            }
        }

        val entitlementToken = obj["entitlements_token"]
            ?.jsonPrimitiveOrNull
            ?.takeIf { it.isString }
            ?.content

        if (entitlementToken.isNullOrBlank()) {
            session.retrieveAuthAccessTokenUnexpectedResponseBody(
                "entitlements_token not found"
            )
            return
        }

        session.entitlement(entitlementToken)
    }
}