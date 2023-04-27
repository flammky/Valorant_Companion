package dev.flammky.valorantcompanion.pvp.party.internal

import dev.flammky.valorantcompanion.auth.riot.RiotAuthService
import dev.flammky.valorantcompanion.auth.riot.RiotGeoRepository
import dev.flammky.valorantcompanion.auth.riot.region.RiotRegion
import dev.flammky.valorantcompanion.pvp.PVPClient
import dev.flammky.valorantcompanion.pvp.ex.UnexpectedResponseException
import dev.flammky.valorantcompanion.pvp.ext.jsonObjectOrNull
import dev.flammky.valorantcompanion.pvp.ext.jsonPrimitiveOrNull
import dev.flammky.valorantcompanion.pvp.http.HttpClient
import dev.flammky.valorantcompanion.pvp.http.JsonHttpRequest
import dev.flammky.valorantcompanion.pvp.http.JsonHttpResponse
import dev.flammky.valorantcompanion.pvp.party.*
import dev.flammky.valorantcompanion.pvp.party.ex.PlayerPartyNotFoundException
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class DisposablePartyServiceClient(
    // TODO: client builder instead
    private val httpClient: HttpClient,
    private val authService: RiotAuthService,
    private val geoRepository: RiotGeoRepository
) : PartyServiceClient {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun fetchSignedInPlayerPartyMembersAsync(puuid: String): Deferred<List<PlayerPartyMember>> {
        val def = CompletableDeferred<List<PlayerPartyMember>>()

        coroutineScope.launch(SupervisorJob()) {
            def.completeWith(
                runCatching {
                    val partyId = getPartyId(puuid).getOrThrow()
                    val partyData = getPlayerPartyData(puuid, partyId).getOrThrow()
                    partyData.members
                }
            )
        }.invokeOnCompletion { ex -> ex?.let { def.completeExceptionally(ex) } }

        return def
    }

    override fun dispose() {

    }

    private suspend fun getPartyId(
        puuid: String
    ): Result<String> {
        return runCatching {
            val geo = geoRepository
                .getGeoShardInfo(puuid)
                ?: error("Geo info not registered")
            val entitlement = authService
                .get_entitlement_token(puuid)
                .getOrThrow()
            val access_token = authService
                .get_authorization(puuid)
                .getOrThrow().access_token
            val url = "https://glz-${geo.region.assignedUrlName}-1.${geo.shard.assignedUrlName}.a.pvp.net/parties/v1/players/$puuid"
            val response = httpClient.jsonRequest(
                JsonHttpRequest(
                    method = "GET",
                    url = url,
                    headers = listOf(
                        "X-Riot-ClientVersion" to PVPClient.VERSION,
                        "X-Riot-Entitlements-JWT" to entitlement,
                        "Authorization" to "Bearer $access_token"
                    ),
                    body = null
                )
            )

            if (response.statusCode == 404) {
                response.body.jsonObjectOrNull?.get("errorCode")
                    ?.jsonPrimitiveOrNull
                    ?.toString()
                    ?.removeSurrounding("\"")
                    ?.let { code ->
                        if (code == "RESOURCE_NOT_FOUND") throw PlayerPartyNotFoundException()
                    }
                unexpectedResponse("Unable to fetch Player Party Info (404)")
            }

            val obj = response.body.jsonObject
            val str = obj["CurrentPartyID"]?.jsonPrimitive?.toString()
            if (str.isNullOrBlank()) {
                unexpectedResponse("CurrentPartyID not found")
            }
            str.removeSurrounding("\"")
        }
    }

    private suspend fun getPlayerPartyData(
        puuid: String,
        partyId: String
    ): Result<PlayerPartyData> {
        return runCatching {
            val geo = geoRepository
                .getGeoShardInfo(puuid)
                ?: error("Geo info not registered")
            val entitlement = authService
                .get_entitlement_token(puuid)
                .getOrThrow()
            val access_token = authService
                .get_authorization(puuid)
                .getOrThrow().access_token
            val url = "https://glz-${geo.region.assignedUrlName}-1.${geo.shard.assignedUrlName}.a.pvp.net/parties/v1/parties/$partyId"
            val response = httpClient.jsonRequest(
                JsonHttpRequest(
                    method = "GET",
                    url = url,
                    headers = listOf(
                        "X-Riot-Entitlements-JWT" to entitlement,
                        "Authorization" to "Bearer $access_token"
                    ),
                    body = null
                )
            )
            parsePlayerPartyDataFromResponse(response).getOrThrow()
        }
    }

    private fun parsePlayerPartyDataFromResponse(
        response: JsonHttpResponse
    ): Result<PlayerPartyData> {
        return runCatching {
            PlayerPartyData(
                party_id = run {
                    response.body.jsonObject["ID"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("ID not found")
                },
                version = run {
                    val str = response.body.jsonObject["Version"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("Version not found")
                    if (!str.all(Char::isDigit)) {
                        unexpectedResponse("Version contains non Digit Char")
                    }
                    str.toLong()
                },
                client_version = run {
                    response.body.jsonObject["ClientVersion"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("ClientVersion not found")
                },
                members = run {
                    val arr = response.body.jsonObject["Members"]?.jsonArray
                        ?: unexpectedResponse("Members not found")
                    arr.map { element ->
                        val member = element.jsonObject
                        PlayerPartyMember(
                            uuid = member["Subject"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?.takeIf { it.isNotBlank() }
                                ?: unexpectedResponse("Subject not found"),
                            competitiveTier = run {
                                val str = member["CompetitiveTier"]?.jsonPrimitive
                                    ?.toString()?.removeSurrounding("\"")
                                    ?: unexpectedResponse("CompetitiveTier not found")
                                if (!str.all(Char::isDigit)) {
                                    unexpectedResponse("Version contains non Digit char")
                                }
                                str.toInt()
                            },
                            identity = run {
                                val identity = member["PlayerIdentity"]?.jsonObject
                                    ?: unexpectedResponse("PlayerIdentity not found")
                                PlayerPartyMemberIdentity(
                                    uuid = run {
                                        identity["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?.takeIf { it.isNotBlank() }
                                            ?: unexpectedResponse("Subject not found")
                                    },
                                    cardId = run {
                                        identity["PlayerCardID"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("PlayerCardID not found")
                                    },
                                    titleId = run {
                                        identity["PlayerTitleID"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("PlayerTitleID not found")
                                    },
                                    accountLevel = run {
                                        val str = identity["AccountLevel"]?.jsonPrimitive
                                            ?.toString()
                                            ?: unexpectedResponse("AccountLevel not found")
                                        if (!str.all(Char::isDigit)) {
                                            unexpectedResponse("AccountLevel contains non Digit char")
                                        }
                                        str.toInt()
                                    },
                                    preferred_level_border_id = run {
                                        identity["PreferredLevelBorderID"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("PreferredLevelBorderID not found")
                                    },
                                    incognito = run {
                                        val str = identity["Incognito"]?.jsonPrimitive
                                            ?.toString()
                                            ?: unexpectedResponse("Incognito not found")
                                        str.toBooleanStrictOrNull()
                                            ?: unexpectedResponse("Incognito is not a Boolean")
                                    },
                                    hideAccountLevel = run {
                                        val str = identity["HideAccountLevel"]?.jsonPrimitive
                                            ?.toString()
                                            ?: unexpectedResponse("HideAccountLevel not found")
                                        str.toBooleanStrictOrNull()
                                            ?: unexpectedResponse("HideAccountLevel is not a Boolean")
                                    }
                                )
                            },
                            seasonalBadgeInfo = run {
                                member["SeasonalBadgeInfo"]
                                    ?: unexpectedResponse("SeasonalBadgeInfo not found")
                            },
                            isOwner = run {
                                val str = member["IsOwner"]?.jsonPrimitive?.toString()
                                    ?: unexpectedResponse("IsOwner not found")
                                str.toBooleanStrictOrNull()
                                    ?: unexpectedResponse("IsOwner is not a Boolean")
                            },
                            queueEligibleRemainingAccountLevels = run {
                                val str = member["QueueEligibleRemainingAccountLevels"]?.jsonPrimitive?.toString()
                                    ?: unexpectedResponse("QueueEligibleRemainingAccountLevels not found")
                                if (!str.all(Char::isDigit)) {
                                    unexpectedResponse("QueueEligibleRemainingAccountLevels contains non digit char")
                                }
                                str.toInt()
                            },
                            pings = run {
                                val pings = member["Pings"]?.jsonArray
                                    ?: unexpectedResponse("Pings not found")
                                pings.map { element ->
                                    PlayerPartyMemberPing(
                                        pingMs = run {
                                            val str = element.jsonObject["Ping"]?.jsonPrimitive?.toString()
                                                ?: unexpectedResponse("Ping not found")
                                            if (!str.all(Char::isDigit)) {
                                                unexpectedResponse("Ping contains non digit char")
                                            }
                                            str.toInt()
                                        },
                                        gamePodId = run {
                                            element.jsonObject["GamePodID"]?.jsonPrimitive
                                                ?.toString()
                                                ?.removeSurrounding("\"")
                                                ?: unexpectedResponse("GamePodID not found")
                                        }
                                    )
                                }
                            },
                            isReady = run {
                                val str = member["IsReady"]?.jsonPrimitive?.toString()
                                    ?: unexpectedResponse("IsReady not found")
                                str.toBooleanStrictOrNull()
                                    ?: unexpectedResponse("IsReady is not a Boolean")
                            },
                            isModerator = run {
                                val str = member["IsModerator"]?.jsonPrimitive?.toString()
                                    ?: unexpectedResponse("IsModerator not found")
                                str.toBooleanStrictOrNull()
                                    ?: unexpectedResponse("IsModerator is not a Boolean")
                            },
                            useBroadcastHUD = run {
                                val str = member["UseBroadcastHUD"]?.jsonPrimitive?.toString()
                                    ?: unexpectedResponse("UseBroadcastHUD not found")
                                str.toBooleanStrictOrNull()
                                    ?: unexpectedResponse("UseBroadcastHUD is not a Boolean")
                            },
                            platformType = run {
                                member["PlatformType"]?.jsonPrimitive
                                    ?.toString()
                                    ?.removeSurrounding("\"")
                                    ?: unexpectedResponse("PlatformType not found")
                            }
                        )
                    }
                },
                state = run {
                    response.body.jsonObject["State"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("State not found")
                },
                previousState = run {
                    response.body.jsonObject["PreviousState"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("PreviousState not found")
                },
                stateTransitionReason = run {
                    response.body.jsonObject["StateTransitionReason"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("StateTransitionReason not found")
                },
                accessibility = run {
                    val str = response.body.jsonObject["Accessibility"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("Accessibility not found")
                    PlayerPartyAccessibility.fromStrStrict(str)
                        ?: unexpectedResponse("Accessibility is not defined locally")
                },
                customGameData = run {
                    val data = response.body.jsonObject["CustomGameData"]?.jsonObject
                        ?: unexpectedResponse("CustomGameData not found")
                    CustomGameData(
                        settings = run {
                            val obj = data["Settings"]?.jsonObject
                                ?: unexpectedResponse("Settings not found")
                            CustomGameDataSettings(
                                map = run {
                                    obj["Map"]?.jsonPrimitive
                                        ?.toString()
                                        ?.removeSurrounding("\"")
                                        ?: unexpectedResponse("Map not found")
                                },
                                mode = run {
                                    obj["Mode"]?.jsonPrimitive
                                        ?.toString()
                                        ?.removeSurrounding("\"")
                                        ?: unexpectedResponse("Mode not found")
                                },
                                useBots = run {
                                    val str = obj["UseBots"]?.jsonPrimitive?.toString()
                                        ?: unexpectedResponse("UseBots not found")
                                    str.toBooleanStrictOrNull()
                                        ?: unexpectedResponse("UseBots is not a Boolean")
                                },
                                gamePod = run {
                                    obj["GamePod"]?.jsonPrimitive
                                        ?.toString()
                                        ?.removeSurrounding("\"")
                                        ?: unexpectedResponse("GamePod not found")
                                },
                            )
                        },
                        membership = run {
                            val obj = data["Membership"]?.jsonObject
                                ?: unexpectedResponse("Membership not found")
                            CustomGameDataMembership(
                                teamOne = run {
                                    val arr = obj["teamOne"]?.let {
                                        if (it is JsonNull) return@run null
                                        it.jsonArray
                                    } ?: unexpectedResponse("teamOne not found")
                                    arr.map {
                                        it.jsonObject["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("Subject not found")
                                    }
                                },
                                teamTwo = run {
                                    val arr = obj["teamTwo"]?.let {
                                        if (it is JsonNull) return@run null
                                        it.jsonArray
                                    } ?: unexpectedResponse("teamTwo not found")
                                    arr.map {
                                        it.jsonObject["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("Subject not found")
                                    }
                                },
                                teamSpectate = run {
                                    val arr = obj["teamSpectate"]?.let {
                                        if (it is JsonNull) return@run null
                                        it.jsonArray
                                    } ?: unexpectedResponse("teamSpectate not found")
                                    arr.map {
                                        it.jsonObject["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("Subject not found")
                                    }
                                },
                                teamOneCoaches = run {
                                    val arr = obj["teamOneCoaches"]?.let {
                                        if (it is JsonNull) return@run null
                                        it.jsonArray
                                    } ?: unexpectedResponse("teamOncCoaches not found")
                                    arr.map {
                                        it.jsonObject["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("Subject not found")
                                    }
                                },
                                teamTwoCoaches = run {
                                    val arr = obj["teamTwoCoaches"]?.let {
                                        if (it is JsonNull) return@run null
                                        it.jsonArray
                                    } ?: unexpectedResponse("teamTwoCoaches not found")
                                    arr.map {
                                        it.jsonObject["Subject"]?.jsonPrimitive
                                            ?.toString()
                                            ?.removeSurrounding("\"")
                                            ?: unexpectedResponse("Subject not found")
                                    }
                                },
                            )
                        },
                        maxPartySize = run {
                            val str = data["MaxPartySize"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?: unexpectedResponse("MaxPartySize not found")
                            if (!str.all(Char::isDigit)) {
                                unexpectedResponse("MaxPartySize contains non Digit Char")
                            }
                            str.toInt()
                        },
                        autobalanceEnabled = run {
                            val str = data["AutobalanceEnabled"]?.jsonPrimitive
                                ?.toString()
                                ?: unexpectedResponse("AutobalanceEnabled not found")
                            str.toBooleanStrictOrNull()
                                ?: unexpectedResponse("AutobalanceEnabled is not a Boolean")
                        },
                        autobalanceMinPlayers = run {
                            val str = data["AutobalanceMinPlayers"]?.jsonPrimitive
                                ?.toString()
                                ?: unexpectedResponse("AutobalanceMinPlayers not found")
                            if (!str.all(Char::isDigit)) {
                                unexpectedResponse("AutobalanceMinPlayers contains non Digit Char")
                            }
                            str.toInt()
                        },
                        hasRecoveryData = run {
                            val str = data["HasRecoveryData"]?.jsonPrimitive
                                ?.toString()
                                ?: unexpectedResponse("HasRecoveryData not found")
                            str.toBooleanStrictOrNull()
                                ?: unexpectedResponse("HasRecoveryData is not a Boolean")
                        },
                    )
                },
                matchmakingData = run {
                    val data = response.body.jsonObject["MatchmakingData"]
                        ?.jsonObject
                        ?: unexpectedResponse("MatchmakingData not found")
                    MatchmakingData(
                        queueId = run {
                            data["QueueID"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?: unexpectedResponse("QueueID not found")
                        },
                        preferredGamePods = run {
                            val arr = data["PreferredGamePods"]?.jsonArray
                                ?: unexpectedResponse("PreferredGamePods not found")
                            arr.map {
                                it.jsonPrimitive
                                    .toString()
                                    .removeSurrounding("\"")
                            }
                        },
                        skillDisparityRRPenalty = run {
                            data["SkillDisparityRRPenalty"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?: unexpectedResponse("SkillDisparityRRPenalty not found")
                        }
                    )
                },
                invites = run {
                    val j = response.body.jsonObject["Invites"]
                        ?: unexpectedResponse("Invites not found")
                    j as? JsonNull
                },
                requests = run {
                    response.body.jsonObject["Requests"]?.jsonArray
                        ?: unexpectedResponse("Requests not found")
                },
                queueEntryTime = run {
                    response.body.jsonObject["QueueEntryTime"]?.jsonPrimitive
                        ?.toString()
                        ?.removeSurrounding("\"")
                        ?: unexpectedResponse("QueueEntryTime not found")
                },
                errorNotification = run {
                    val data = response.body.jsonObject["ErrorNotification"]?.jsonObject
                        ?: unexpectedResponse("ErrorNotification not found")
                    ErrorNotification(
                        errorType = run {
                            data["ErrorType"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?: unexpectedResponse("ErrorType not found")
                        },
                        erroredPlayers = run {
                            val r = data["ErroredPlayers"]
                                ?: unexpectedResponse("ErroredPlayers not found")
                            if (r is JsonNull) return@run null
                            r.jsonArray.map { it.jsonPrimitive.toString() }
                        }
                    )
                },
                restrictedSeconds = run {
                    val str = response.body.jsonObject["RestrictedSeconds"]?.jsonPrimitive
                        ?.toString()
                        ?: unexpectedResponse("RestrictedSeconds not found")
                    if (str.isBlank()) {
                        unexpectedResponse("RestrictedSeconds is empty")
                    }
                    if (!str.all(Char::isDigit)) {
                        unexpectedResponse("RestrictedSeconds contains non digit number")
                    }
                    str.toInt()
                },
                eligibleQueues = run {
                    val arr = response.body.jsonObject["EligibleQueues"]?.jsonArray
                        ?: unexpectedResponse("EligibleQueues not found")
                    arr.map { it.jsonPrimitive.toString().removeSurrounding("\"") }
                },
                queueIneligibilities = run {
                    val arr = response.body.jsonObject["QueueIneligibilities"]?.jsonArray
                        ?: unexpectedResponse("QueueIneligibilities not found")
                    arr.map { it.jsonPrimitive.toString().removeSurrounding("\"") }
                },
                cheatData = run {
                    val data = response.body.jsonObject["CheatData"]?.jsonObject
                        ?: unexpectedResponse("CheatData not found")
                    CheatData(
                        gamePodOverride = run {
                            data["GamePodOverride"]?.jsonPrimitive
                                ?.toString()
                                ?.removeSurrounding("\"")
                                ?: unexpectedResponse("GamePodOverride not found")
                        },
                        forcePostGameProcessing = run {
                            val str = data["ForcePostGameProcessing"]?.jsonPrimitive
                                ?.toString()
                                ?: unexpectedResponse("ForcePostGameProcessing not found")
                            str.toBooleanStrictOrNull()
                                ?: unexpectedResponse("ForcePostGameProcessing is not a Boolean")
                        }
                    )
                },
                xpBonuses = run {
                    val arr = response.body.jsonObject["XPBonuses"]?.jsonArray
                        ?: unexpectedResponse("XPBonuses not found")
                    arr
                },
            )
        }
    }

    private fun unexpectedResponse(msg: String): Nothing {
        throw UnexpectedResponseException(msg)
    }
}