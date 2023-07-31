package dev.flammky.valorantcompanion.assets.map

import dev.flammky.valorantcompanion.assets.http.AssetHttpClient
import dev.flammky.valorantcompanion.base.storage.ByteUnit
import dev.flammky.valorantcompanion.base.storage.kiloByteUnit
import dev.flammky.valorantcompanion.base.storage.megaByteUnit
import dev.flammky.valorantcompanion.base.storage.noIntOverflow
import io.ktor.util.*
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.*
import java.nio.ByteBuffer

class ValorantMapAssetDownloader(
    private val assetHttpClient: AssetHttpClient,
    private val endpoint: ValorantMapAssetEndpoint
) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    fun downloadImage(
        id: String,
        types: ImmutableSet<ValorantMapImageType>
    ): ValorantMapAssetDownloadInstance {
        return ValorantMapAssetDownloadInstance(
            id,
            types
        ).apply { initiateDownloadForInstance(this) }
    }

    private fun initiateDownloadForInstance(
        instance: ValorantMapAssetDownloadInstance
    ) {
        coroutineScope.launch(Dispatchers.IO) {

            instance.types.forEach { type ->
                runCatching {

                    var result: ByteArray? = null

                    val contentSizeLimit = contentSizeLimitOfImageType(type)
                        .noIntOverflow()
                        .bytes()
                        .toInt()

                    assetHttpClient.get(
                        url = endpoint.buildImageUrl(instance.id, type),
                        sessionHandler = handler@ {
                            val contentLength = contentLength

                            val bb = when {
                                contentLength == null -> {
                                    ByteBuffer.allocate(contentSizeLimit)
                                }
                                contentSizeLimit >= contentLength -> {
                                    ByteBuffer.allocate(contentLength.toInt())
                                }
                                else -> return@handler reject()
                            }
                            consume(bb)
                            result = bb.apply { flip() }.moveToByteArray()
                        }
                    )

                    result?.let { arr ->
                        instance.completeWith(
                            Result.success(
                                ValorantMapImage(
                                    instance.id,
                                    type,
                                    arr
                                )
                            )
                        )
                        return@launch
                    }
                }
            }

            // TODO: differentiate between remote error and local error
            instance.completeWith(
                Result.failure(
                    CancellationException("None of the acceptable types were available")
                )
            )

        }.invokeOnCompletion { ex ->
            ex?.let { instance.completeWith(Result.failure(ex)) }
            check(instance.isCompleted)
        }
    }

    companion object {

        fun contentSizeLimitOfImageType(type: ValorantMapImageType): ByteUnit {
            return if (type is ValorantMapImageType.Splash) {
                3.megaByteUnit()
            }  else {
                100.kiloByteUnit()
            }
        }
    }
}