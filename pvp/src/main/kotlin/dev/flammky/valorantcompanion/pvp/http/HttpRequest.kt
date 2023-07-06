package dev.flammky.valorantcompanion.pvp.http

import kotlinx.serialization.json.JsonElement

class JsonHttpRequest(
    val method: String,
    val url: String,
    val headers: List<Pair<String, String>>,
    val body: JsonElement?,
)

// TODO
class StreamJsonHttpRequest()