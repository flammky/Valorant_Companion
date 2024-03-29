package dev.flammky.valorantcompanion.auth.riot.internal

import dev.flammky.valorantcompanion.auth.ex.AuthFailureException
import dev.flammky.valorantcompanion.auth.ex.InvalidSessionException
import dev.flammky.valorantcompanion.auth.ex.ResponseParsingException
import dev.flammky.valorantcompanion.auth.ex.UnexpectedResponseException
import dev.flammky.valorantcompanion.auth.ext.jsonObjectOrNull
import dev.flammky.valorantcompanion.auth.ext.jsonPrimitiveOrNull
import dev.flammky.valorantcompanion.auth.riot.AuthHttpRequestResponse
import dev.flammky.valorantcompanion.auth.riot.AuthRequestResponseData
import dev.flammky.valorantcompanion.base.kt.substringAfterOrNull
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

internal suspend fun retrieveAuthAccessToken(
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

    val uriString = obj["response"]
        ?.jsonObjectOrNull?.get("parameters")
        ?.jsonObjectOrNull?.get("uri")
        ?.jsonPrimitiveOrNull
        ?.takeIf { it.isString }
        ?.content

    if (uriString == null) {
        session.onException(
            ResponseParsingException("uri not fround")
        )
        return
    }

    val access_token = uriString
        .substringAfterOrNull("access_token=")
        ?.takeWhile { it != '&' }

    if (access_token == null || access_token.isBlank()) {
        session.onException(
            ResponseParsingException("access_token not found")
        )
        return
    }

    val id_token = uriString
        .substringAfterOrNull("id_token=")
        ?.takeWhile { it != '&' }

    if (id_token == null || id_token.isBlank()) {
        session.onException(
            ResponseParsingException("id_token not found")
        )
        return
    }

    session.parsedData(
        AuthRequestResponseData(access_token, id_token)
    )
}

internal suspend fun retrieveReAuthAccessToken(
    session: RiotJointReauthorizeSessionImpl
) {
    val statusCode = session._reAuthStatusCode.value
    val responseBody = session._reAuthResponseBody.value
    if (statusCode == null) {
        return
    }
    if (statusCode !in 200..299) {
        session.retrieveAuthAccessTokenUnexpectedHttpStatusCode(
            statusCode
        )
        return
    }
    if (responseBody == null) {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "Body is null"
        )
        return
    }
    val element = runCatching {
        Json.parseToJsonElement(String(responseBody))
    }.fold(
        onFailure = { ex ->
            if (ex is SerializationException) {
                session.retrieveAuthAccessTokenUnexpectedResponseBody("Body is not a JSON")
            }
            session.retrieveAuthAccessTokenUnexpectedException(
                "Exception while deserializing ReAuth Response Body"
            )
            return
        },
        onSuccess = { it }
    )
    val obj = element.jsonObjectOrNull
        ?: run {
            session.retrieveAuthAccessTokenUnexpectedResponseBody(
                "expected JSON object, but got ${element::class::simpleName} instead"
            )
            return
        }

    if (obj["error"]?.jsonPrimitive?.content == "auth_failure") {
        session.retrieveAuthAccessTokenAuthError(
            "AuthFailure"
        )
        return
    }

    if (obj["error"]?.jsonPrimitive?.content == "invalid_session_id") {
        session.retrieveAuthAccessTokenSessionError(
            "invalid_session_id"
        )
        return
    }

    val uriString = obj["response"]
        ?.jsonObjectOrNull?.get("parameters")
        ?.jsonObjectOrNull?.get("uri")
        ?.jsonPrimitiveOrNull
        ?.takeIf { it.isString }
        ?.content

    if (uriString == null) {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "uri not found"
        )
        return
    }

    val access_token = uriString
        .substringAfterOrNull("access_token=")
        ?.takeWhile { it != '&' }

    if (access_token == null || access_token.isBlank()) {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "access_token not found"
        )
        return
    }

    val id_token = uriString
        .substringAfterOrNull("id_token=")
        ?.takeWhile { it != '&' }

    if (id_token == null || id_token.isBlank()) {
        session.retrieveAuthAccessTokenUnexpectedResponseBody(
            "id_token not found"
        )
        return
    }

    session.reAuthCookieResponseParsed(
        access_token = access_token,
        id_token = id_token
    )
}