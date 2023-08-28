package dev.flammky.valorantcompanion.live.loadout.presentation

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.flammky.valorantcompanion.assets.ValorantAssetsService
import dev.flammky.valorantcompanion.assets.debug.DebugValorantAssetService
import dev.flammky.valorantcompanion.auth.riot.DebugRiotAuthRepository
import dev.flammky.valorantcompanion.auth.riot.RiotAccountModel
import dev.flammky.valorantcompanion.auth.riot.RiotAuthRepository
import dev.flammky.valorantcompanion.auth.riot.RiotAuthenticatedAccount
import dev.flammky.valorantcompanion.base.di.compose.LocalDependencyInjector
import dev.flammky.valorantcompanion.base.di.koin.compose.KoinDependencyInjector
import dev.flammky.valorantcompanion.base.theme.material3.DefaultMaterial3Theme
import dev.flammky.valorantcompanion.live.BuildConfig
import dev.flammky.valorantcompanion.pvp.loadout.*
import dev.flammky.valorantcompanion.pvp.loadout.debug.StubValorantPlayerLoadoutService
import dev.flammky.valorantcompanion.pvp.loadout.spray.PvpSpray
import dev.flammky.valorantcompanion.pvp.loadout.spray.PvpSprayConstants
import dev.flammky.valorantcompanion.pvp.loadout.spray.knownSlotsUUIDtoOrderedList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

@Composable
@Preview
internal fun SprayLoadoutScreenPreview() {
    val provisionedState = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(
        key1 = Unit,
        block = {
            GlobalContext.stopKoin()
            GlobalContext.startKoin {
                modules(
                    module {
                        single<ValorantAssetsService>() {
                            DebugValorantAssetService()
                        }
                        single<RiotAuthRepository>() {
                            DebugRiotAuthRepository().apply {
                                registerAuthenticatedAccount(
                                    RiotAuthenticatedAccount(
                                    RiotAccountModel("Dokka", "Dokka", "Dokka", "301")
                                ), true)
                            }
                        }
                        single<PlayerLoadoutService>() {
                            val mutex = Mutex()
                            var loadout = PlayerLoadout(
                                "Dokka",
                                0,
                                persistentListOf(),
                                persistentListOf(
                                    SprayLoadoutItem(
                                        PvpSprayConstants.SLOT_1_UUID,
                                        "<3"
                                    ),
                                    SprayLoadoutItem(
                                        PvpSprayConstants.SLOT_2_UUID,
                                        "nice_to_zap_you"
                                    ),
                                    SprayLoadoutItem(
                                        PvpSprayConstants.SLOT_3_UUID,
                                        "<3"
                                    ),
                                    SprayLoadoutItem(
                                        PvpSprayConstants.SLOT_4_UUID,
                                        "nice_to_zap_you"
                                    )
                                ),
                                IdentityLoadout(
                                    "",
                                    "",
                                    0,
                                    "",
                                    false
                                ),
                                incognito = false
                            )
                            StubValorantPlayerLoadoutService(
                                loadoutProvider = { puuid ->
                                    mutex.withLock { loadout }
                                },
                                availableLoadoutProvider = { puuid ->
                                    // TODO: NO_SPRAY
                                    PlayerAvailableSprayLoadout(
                                        persistentListOf(
                                            "<3",
                                            "nice_to_zap_you",
                                        )
                                    )
                                },
                                loadoutModifyHandler = { uuid, changed ->
                                    Log.d(
                                        BuildConfig.LIBRARY_PACKAGE_NAME,
                                        "loadout.presentation.layoutModifyHandler($uuid, $changed)"
                                    )
                                    mutex.withLock {
                                        delay(200)
                                        val new = PlayerLoadout(
                                            puuid = loadout.puuid,
                                            version = loadout.version + 1,
                                            guns = changed.guns,
                                            sprays = changed.sprays/*.let { sprays ->
                                                val res = persistentListOf<SprayLoadoutItem>().builder()
                                                val knownSlots = PvpSpray.knownSlotsUUIDtoOrderedList()
                                                val known = mutableListOf<SprayLoadoutItem>()
                                                val extra = mutableListOf<SprayLoadoutItem>()
                                                knownSlots.forEach { slotId ->
                                                    sprays.find { it.equipSlotId == slotId }
                                                        ?.let { known.add(it) }
                                                }
                                                sprays.forEach {
                                                    if (it.equipSlotId !in knownSlots)
                                                        extra.add(it)
                                                }
                                                known.forEach { res.add(it) }
                                                extra.forEach { res.add(it) }
                                                res.build()
                                            }*/,
                                            identity = changed.identity,
                                            incognito = changed.incognito
                                        )
                                        loadout = new ; loadout
                                    }
                                }
                            )
                        }
                    }
                )
            }
            provisionedState.value = true
        }
    )
    CompositionLocalProvider(
        LocalDependencyInjector provides remember {
            KoinDependencyInjector(GlobalContext)
        }
    ) {
        if (provisionedState.value) DefaultMaterial3Theme(dark = true) {
            SprayLoadoutScreen(
                modifier = Modifier,
                state = SprayLoadoutScreenState(

                ),
                dismiss = {}
            )
        }
    }

}