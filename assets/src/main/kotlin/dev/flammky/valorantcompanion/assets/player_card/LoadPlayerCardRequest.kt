package dev.flammky.valorantcompanion.assets.player_card

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet

class LoadPlayerCardRequest private constructor(
    val uuid: String,
    // ordered
    val acceptableTypes: PersistentSet<PlayerCardArtType>,
) {

    public constructor(
        player_card_id: String,
        vararg acceptableTypes: PlayerCardArtType,
    ) : this(player_card_id, acceptableTypes.asIterable().toPersistentSet())
}