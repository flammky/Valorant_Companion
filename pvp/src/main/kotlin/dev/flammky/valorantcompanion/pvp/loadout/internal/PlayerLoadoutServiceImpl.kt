package dev.flammky.valorantcompanion.pvp.loadout.internal

import dev.flammky.valorantcompanion.pvp.http.HttpClient
import dev.flammky.valorantcompanion.pvp.internal.AuthProvider
import dev.flammky.valorantcompanion.pvp.internal.GeoProvider
import dev.flammky.valorantcompanion.pvp.loadout.PlayerLoadoutService

internal class PlayerLoadoutServiceImpl(
    private val repo: PlayerLoadoutRepositoryImpl,
    private val authProvider: AuthProvider,
    private val geoProvider: GeoProvider,
    private val httpClientFactory: () -> HttpClient
) : PlayerLoadoutService {

    override fun createClient(): DisposablePlayerLoadoutClientImpl {
        return DisposablePlayerLoadoutClientImpl(
            repo,
            authProvider,
            geoProvider,
            httpClientFactory()
        )
    }
}