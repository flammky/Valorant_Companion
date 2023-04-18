package dev.flammky.valorantcompanion.auth.riot

interface RiotAuthService {

    fun loginAsync(
        request: RiotLoginRequest
    ): RiotLoginSession
}