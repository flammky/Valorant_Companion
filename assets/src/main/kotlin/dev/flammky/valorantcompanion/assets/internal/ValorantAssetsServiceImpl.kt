package dev.flammky.valorantcompanion.assets.internal

import android.util.Log
import dev.flammky.valorantcompanion.assets.BuildConfig
import dev.flammky.valorantcompanion.assets.ValorantAssetsLoaderClient
import dev.flammky.valorantcompanion.assets.ValorantAssetsService
import dev.flammky.valorantcompanion.assets.bundle.ValorantBundleAssetDownloader
import dev.flammky.valorantcompanion.assets.ktor.KtorWrappedHttpClient
import dev.flammky.valorantcompanion.assets.map.ValorantApiMapAssetEndpoint
import dev.flammky.valorantcompanion.assets.map.ValorantMapAssetDownloader
import dev.flammky.valorantcompanion.assets.player_card.PlayerCardAssetDownloader
import dev.flammky.valorantcompanion.assets.player_card.ValorantApiPlayerCardAssetEndpoint
import dev.flammky.valorantcompanion.assets.player_title.ConflatingValorantTitleAssetLoader
import dev.flammky.valorantcompanion.assets.player_title.DelegatedValorantTitleAssetLoader
import dev.flammky.valorantcompanion.assets.player_title.PlayerTitleAssetDownloaderImpl
import dev.flammky.valorantcompanion.assets.player_title.PlayerTitleAssetEndpointResolverImpl
import dev.flammky.valorantcompanion.assets.spray.KtxValorantSprayAssetSerializer
import dev.flammky.valorantcompanion.assets.spray.ValorantApiSprayAssetEndpoint
import dev.flammky.valorantcompanion.assets.spray.ValorantApiSprayResponseParser
import dev.flammky.valorantcompanion.assets.spray.ValorantSprayAssetDownloader
import dev.flammky.valorantcompanion.assets.valcom.playertitle.ValComPlayerTitleAssetDownloader
import dev.flammky.valorantcompanion.assets.valcom.playertitle.ValComPlayerTitleAssetEndpoint
import dev.flammky.valorantcompanion.assets.valorantapi.bundle.ValorantApiBundleAssetEndpoint
import dev.flammky.valorantcompanion.assets.valorantapi.bundle.ValorantApiBundleAssetParser
import dev.flammky.valorantcompanion.assets.valorantapi.gunbuddy.ValorantApiGunBuddyAssetEndpoint
import dev.flammky.valorantcompanion.assets.valorantapi.playertitle.ValorantApiTitleAssetDownloader
import dev.flammky.valorantcompanion.assets.valorantapi.playertitle.ValorantApiTitleAssetEndpoint
import dev.flammky.valorantcompanion.assets.weapon.gunbuddy.ValorantGunBuddyAssetDownloaderImpl
import dev.flammky.valorantcompanion.assets.weapon.gunbuddy.ValorantGunBuddyAssetLoaderImpl
import dev.flammky.valorantcompanion.assets.weapon.skin.ValorantWeaponSkinAssetDownloaderImpl
import dev.flammky.valorantcompanion.assets.weapon.skin.ValorantWeaponSkinAssetLoaderImpl
import dev.flammky.valorantcompanion.assets.weapon.skin.WeaponSkinAssetEndpointResolverImpl
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.logging.*

class ValorantAssetsServiceImpl(
    private val repo: ValorantAssetRepository
) : ValorantAssetsService {

    private val httpClient by lazy {
        KtorWrappedHttpClient(
            self = HttpClient(OkHttp) {
                if (BuildConfig.DEBUG) {
                    install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                Log.i("ASSET_KtorHttpClient", message)
                            }
                        }
                        level = LogLevel.ALL
                    }
                }
            }
        )
    }

    private val conflatingTitleAssetLoader = ConflatingValorantTitleAssetLoader(
        repository = repo,
        downloader = PlayerTitleAssetDownloaderImpl(
            endpointResolver = PlayerTitleAssetEndpointResolverImpl(::httpClient::get),
            endpointDownloader = { endpoint ->
                when(endpoint) {
                    is ValComPlayerTitleAssetEndpoint -> ValComPlayerTitleAssetDownloader(::httpClient::get)
                    is ValorantApiTitleAssetEndpoint -> ValorantApiTitleAssetDownloader(::httpClient::get)
                    else -> null
                }
            }
        )
    )

    override fun createLoaderClient(): ValorantAssetsLoaderClient {
        return DisposableValorantAssetsLoaderClient(
            repository = repo,
            player_card_downloader = PlayerCardAssetDownloader(
                httpClient,
                ValorantApiPlayerCardAssetEndpoint()
            ),
            map_asset_downloader = ValorantMapAssetDownloader(
                httpClient,
                ValorantApiMapAssetEndpoint()
            ),
            spray_asset_downloader = ValorantSprayAssetDownloader(
                httpClient,
                ValorantApiSprayAssetEndpoint(),
                ValorantApiSprayResponseParser(KtxValorantSprayAssetSerializer())
            ),
            bundle_asset_downloader = ValorantBundleAssetDownloader(
                httpClient,
                ValorantApiBundleAssetEndpoint(),
                ValorantApiBundleAssetParser()
            ),
            gunBuddyAssetLoader = ValorantGunBuddyAssetLoaderImpl(
                repository = repo,
                downloader = ValorantGunBuddyAssetDownloaderImpl(
                    assetHttpClient = httpClient,
                    endpoint = ValorantApiGunBuddyAssetEndpoint()
                )
            ),
            weaponSkinAssetLoader = ValorantWeaponSkinAssetLoaderImpl(
                repository = repo,
                downloader = ValorantWeaponSkinAssetDownloaderImpl(
                    endpointResolver = WeaponSkinAssetEndpointResolverImpl(
                        httpClientFactory = { httpClient }
                    ),
                    httpClientFactory = { httpClient }
                )
            ),
            tittleAssetLoader = DelegatedValorantTitleAssetLoader(conflatingTitleAssetLoader)
        )
    }
}