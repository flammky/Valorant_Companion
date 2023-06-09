package dev.flammky.valorantcompanion.assets.di

import dev.flammky.valorantcompanion.assets.ValorantAssetsService
import dev.flammky.valorantcompanion.assets.filesystem.AndroidFileSystem
import dev.flammky.valorantcompanion.assets.internal.ValorantAssetRepository
import dev.flammky.valorantcompanion.assets.internal.ValorantAssetsServiceImpl
import org.koin.dsl.module

val KoinAssetsModule = module {
    single<ValorantAssetRepository> { ValorantAssetRepository(AndroidFileSystem(get())) }
    single<ValorantAssetsService> { ValorantAssetsServiceImpl(get<ValorantAssetRepository>()) }
}