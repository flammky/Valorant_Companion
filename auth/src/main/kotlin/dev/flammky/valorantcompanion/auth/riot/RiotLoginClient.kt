package dev.flammky.valorantcompanion.auth.riot

interface RiotLoginClient {

    fun login(
        request: RiotLoginRequest,
        setActive: Boolean
    ): RiotLoginSession
}