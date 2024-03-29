package dev.flammky.valorantcompanion.pvp.party

data class PlayerPartyData(
    val party_id: String,
    val version: Long,
    val client_version: String,
    val members: List<PlayerPartyMemberData>,
    val state: String,
    val previousState: String,
    val stateTransitionReason: String,
    val accessibility: PlayerPartyAccessibility,
    val customGameData: CustomGameData,
    val matchmakingData: MatchmakingData,
    val invites: Any? = null,
    val requests: List<Any?>,
    val queueEntryTime: String,
    val errorNotification: ErrorNotification,
    val restrictedSeconds: Int,
    val eligibleQueues: List<String>,
    val queueIneligibilities: List<QueueIneligibilityData>,
    val cheatData: CheatData,
    val xpBonuses: List<Any?>
)
