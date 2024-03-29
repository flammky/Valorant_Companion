package dev.flammky.valorantcompanion.pvp.http

import kotlinx.serialization.json.JsonElement

data class JsonHttpResponse(
    val headers: HttpResponseHeaders,
    val statusCode: Int,
    val body: Result<JsonElement>,
    val getResponseProperty: (String) -> Any?
) {
}