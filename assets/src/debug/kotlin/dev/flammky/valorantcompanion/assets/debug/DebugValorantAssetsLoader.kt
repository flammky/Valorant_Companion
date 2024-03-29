package dev.flammky.valorantcompanion.assets.debug

import dev.flammky.valorantcompanion.assets.*
import dev.flammky.valorantcompanion.assets.bundle.BundleImageIdentifier
import dev.flammky.valorantcompanion.assets.bundle.BundleImageType
import dev.flammky.valorantcompanion.assets.map.MapImageIdentifier
import dev.flammky.valorantcompanion.assets.map.ValorantMapImageType
import dev.flammky.valorantcompanion.assets.player_card.PlayerCardArtType
import dev.flammky.valorantcompanion.assets.player_card.PlayerCardIdentifier
import dev.flammky.valorantcompanion.assets.player_title.PlayerTitleIdentity
import dev.flammky.valorantcompanion.assets.spray.SprayImageIdentifier
import dev.flammky.valorantcompanion.assets.spray.ValorantSprayAssetIdentity
import dev.flammky.valorantcompanion.assets.spray.ValorantSprayImageType
import dev.flammky.valorantcompanion.assets.weapon.skin.WeaponSkinIdentity
import dev.flammky.valorantcompanion.assets.weapon.skin.WeaponSkinImageIdentifier
import dev.flammky.valorantcompanion.assets.weapon.skin.WeaponSkinImageType
import dev.flammky.valorantcompanion.base.debug.debugBlock
import dev.flammky.valorantcompanion.pvp.agent.ValorantAgentIdentity
import dev.flammky.valorantcompanion.pvp.agent.ValorantAgentRole
import dev.flammky.valorantcompanion.pvp.map.ValorantMapIdentity
import dev.flammky.valorantcompanion.pvp.store.currency.KingdomCredit
import dev.flammky.valorantcompanion.pvp.store.currency.RadianitePoint
import dev.flammky.valorantcompanion.pvp.store.currency.StoreCurrency
import dev.flammky.valorantcompanion.pvp.store.currency.ValorantPoint
import dev.flammky.valorantcompanion.pvp.store.weapon.skin.WeaponSkinTier
import dev.flammky.valorantcompanion.pvp.tier.CompetitiveRank
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

// asset service containing debug-only asset resource that might Not packaged onto release
class DebugValorantAssetService : ValorantAssetsService {

    override fun createLoaderClient(): ValorantAssetsLoaderClient {
        return DebugValorantAssetsLoaderClient(
            agentIconMapping = AGENT_ICON_MAPPING,
            roleIconMapping = ROLE_ICON_MAPPING,
            competitiveRankIconMapping = COMPETITIVE_RANK_MAPPING,
            // TODO
            playerCardMapping = ACC_PLAYERCARD_MAPPING,
            mapImageMapping = MAP_MAPPING,
            sprayImageMapping = SPRAY_MAPPING,
            sprayIdentityMapping = SPRAY_IDENTITY_MAPPING,
            bundleImageMapping = BUNDLE_IMAGE_MAPPING,
            currencyImageMapping = CURRENCY_IMAGE_MAPPING,
            weaponSkinIdentityMapping = WEAPON_SKIN_IDENTITY_MAPPING,
            weaponSkinImageMapping = WEAPON_SKIN_IMAGE_MAPPING,
            weaponSkinTierImageMapping = WEAPON_SKIN_TIER_IMAGE_MAPPING,
            gunBuddyImageMapping = WEAPON_GUNBUDDY_IMAGE_MAPPING,
            titleIdentityMapping = TITTLE_IDENTITY_MAPPING
        )
    }

    companion object {

        val AGENT_ICON_MAPPING = run {
            persistentMapOf<String, LocalImage<*>>().builder().apply {
                put(
                    ValorantAgentIdentity.ASTRA.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_astra_displayicon)
                )
                put(
                    ValorantAgentIdentity.BREACH.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_breach_displayicon)
                )
                put(
                    ValorantAgentIdentity.BRIMSTONE.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_brimstone_displayicon)
                )
                put(
                    ValorantAgentIdentity.CHAMBER.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_chamber_displayicon)
                )
                put(
                    ValorantAgentIdentity.CYPHER.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_cypher_displayicon)
                )
                put(
                    ValorantAgentIdentity.FADE.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_fade_displayicon)
                )
                put(
                    ValorantAgentIdentity.GEKKO.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_gekko_displayicon)
                )
                put(
                    ValorantAgentIdentity.HARBOR.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_harbor_displayicon)
                )
                put(
                    ValorantAgentIdentity.JETT.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_jett_displayicon)
                )
                put(
                    ValorantAgentIdentity.KAYO.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_kayo_displayicon)
                )
                put(
                    ValorantAgentIdentity.KILLJOY.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_killjoy_displayicon)
                )
                put(
                    ValorantAgentIdentity.NEON.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_neon_displayicon)
                )
                put(
                    ValorantAgentIdentity.OMEN.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_omen_displayicon)
                )
                put(
                    ValorantAgentIdentity.PHOENIX.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_phoenix_displayicon)
                )
                put(
                    ValorantAgentIdentity.RAZE.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_raze_displayicon)
                )
                put(
                    ValorantAgentIdentity.REYNA.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_reyna_displayicon)
                )
                put(
                    ValorantAgentIdentity.SAGE.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_sage_displayicon)
                )
                put(
                    ValorantAgentIdentity.SKYE.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_skye_displayicon)
                )
                put(
                    ValorantAgentIdentity.SOVA.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_sova_displayicon)
                )
                put(
                    ValorantAgentIdentity.VIPER.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_viper_displayicon)
                )
                put(
                    ValorantAgentIdentity.YORU.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agent_yoru_displayicon)
                )
            }.build()
        }

        val ROLE_ICON_MAPPING = run {
            persistentMapOf<String, LocalImage<*>>().builder().apply {
                put(
                    ValorantAgentRole.Controller.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agentrole_controller_displayicon)
                )
                put(
                    ValorantAgentRole.Duelist.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agentrole_dueslist_displayicon)
                )
                put(
                    ValorantAgentRole.Initiator.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agentrole_initiator_displayicon)
                )
                put(
                    ValorantAgentRole.Sentinel.uuid,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.agentrole_sentinel_displayicon)
                )
            }.build()
        }

        val COMPETITIVE_RANK_MAPPING = run {
            persistentMapOf<CompetitiveRank, LocalImage<*>>().builder().apply {
                put(
                    CompetitiveRank.UNRANKED,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_unranked_smallicon)
                )
                put(
                    CompetitiveRank.UNUSED_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_unranked_smallicon)
                )
                put(
                    CompetitiveRank.UNUSED_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_unranked_smallicon)
                )
                put(
                    CompetitiveRank.IRON_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_iron1_smallicon)
                )
                put(
                    CompetitiveRank.IRON_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_iron2_smallicon)
                )
                put(
                    CompetitiveRank.IRON_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_iron3_smallicon)
                )
                put(
                    CompetitiveRank.BRONZE_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_bronze1_smallicon)
                )
                put(
                    CompetitiveRank.BRONZE_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_bronze2_smallicon)
                )
                put(
                    CompetitiveRank.BRONZE_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_bronze3_smallicon)
                )
                put(
                    CompetitiveRank.SILVER_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_silver1_smallicon)
                )
                put(
                    CompetitiveRank.SILVER_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_silver2_smallicon)
                )
                put(
                    CompetitiveRank.SILVER_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_silver3_smallicon)
                )
                put(
                    CompetitiveRank.GOLD_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_gold1_smallicon)
                )
                put(
                    CompetitiveRank.GOLD_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_gold2_smallicon)
                )
                put(
                    CompetitiveRank.GOLD_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_gold3_smallicon)
                )
                put(
                    CompetitiveRank.PLATINUM_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_platinum1_smallicon)
                )
                put(
                    CompetitiveRank.PLATINUM_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_platinum2_smallicon)
                )
                put(
                    CompetitiveRank.PLATINUM_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_platinum3_smallicon)
                )
                put(
                    CompetitiveRank.DIAMOND_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_diamond1_smallicon)
                )
                put(
                    CompetitiveRank.DIAMOND_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_diamond2_smallicon)
                )
                put(
                    CompetitiveRank.DIAMOND_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_diamond3_smallicon)
                )
                put(
                    CompetitiveRank.ASCENDANT_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_ascendant1_smallicon)
                )
                put(
                    CompetitiveRank.ASCENDANT_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_ascendant2_smallicon)
                )
                put(
                    CompetitiveRank.ASCENDANT_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_ascendant3_smallicon)
                )
                put(
                    CompetitiveRank.IMMORTAL_1,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_immortal1_smallicon)
                )
                put(
                    CompetitiveRank.IMMORTAL_2,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_immortal2_smallicon)
                )
                put(
                    CompetitiveRank.IMMORTAL_3,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_immortal3_smallicon)
                )
                put(
                    CompetitiveRank.IMMORTAL_MERGED,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_immortal3_smallicon)
                )
                put(
                    CompetitiveRank.RADIANT,
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.rank_radiant_smallicon)
                )
            }.build()
        }

        val ACC_PLAYERCARD_MAPPING = debugBlock {
            persistentMapOf<PlayerCardIdentifier, LocalImage<*>>().builder().apply {
                put(
                    PlayerCardIdentifier(
                        "dbg_acc_playercard_yearone",
                        PlayerCardArtType.SMALL
                    ),
                    LocalImage.Resource(R_ASSET_RAW.debug_acc_playercard_yearone_displayicon)
                )
            }
        }

        val MAP_MAPPING = debugBlock {
            persistentMapOf<MapImageIdentifier, LocalImage<*>>().builder().apply {
                put(
                    MapImageIdentifier(
                        uuid = ValorantMapIdentity.ASCENT.map_id,
                        type = ValorantMapImageType.ListView
                    ),
                    LocalImage.Resource(dev.flammky.valorantcompanion.assets.R.raw.debug_ascent_listviewicon)
                )
            }.build()
        }

        val SPRAY_MAPPING = debugBlock {
            persistentMapOf<SprayImageIdentifier, LocalImage<*>>().builder().apply {
                put(
                    SprayImageIdentifier(
                        uuid = "dbg_acc_spray_nice_to_zap_you",
                        type = ValorantSprayImageType.FULL_ICON(transparentBackground = true)
                    ),
                    LocalImage.Resource(R_ASSET_RAW.debug_spray_nice_to_zap_you_transparent)
                )
                put(
                    SprayImageIdentifier(
                        uuid = "dbg_acc_spray_0x3d_3",
                        type = ValorantSprayImageType.FULL_ICON(transparentBackground = true)
                    ),
                    LocalImage.Resource(R_ASSET_RAW.debug_spray_0x3c_3_transparent)
                )
            }.build()
        }

        val SPRAY_IDENTITY_MAPPING = debugBlock {
            persistentMapOf<String, ValorantSprayAssetIdentity>()
                .builder()
                .apply {
                    put(
                        "dbg_acc_nice_to_zap_you",
                        ValorantSprayAssetIdentity(
                            uuid = "dbg_acc_nice_to_zap_you",
                            displayName = "Nice to Zap You Spray",
                            category = ValorantSprayAssetIdentity.Category.Contextual,
                            levels = persistentListOf(
                                ValorantSprayAssetIdentity.Level(
                                    uuid = "dbg_acc_nice_to_zap_you_lv1",
                                    sprayLevel = 1,
                                    displayName = "SprinterShock"
                                )
                            )
                        )
                    )
                    put(
                        "dbg_acc_spray_0x3c_3",
                        ValorantSprayAssetIdentity(
                            uuid = "dbg_acc_spray_0x3c_3",
                            displayName = "<3 Spray",
                            category = ValorantSprayAssetIdentity.Category.Unspecified,
                            levels = persistentListOf(
                                ValorantSprayAssetIdentity.Level(
                                    uuid = "dbg_acc_spray_0x3c_3_lv1",
                                    sprayLevel = 1,
                                    displayName = "<3 Spray"
                                )
                            )
                        )
                    )
                }
                .build()
        }

        val BUNDLE_IMAGE_MAPPING = debugBlock {
            persistentMapOf<BundleImageIdentifier, LocalImage<*>>()
                .builder()
                .apply {
                    put(
                        BundleImageIdentifier(
                            "neofrontier",
                            BundleImageType.DISPLAY
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_bundle_neo_frontier)
                    )
                }
                .build()
        }

        val CURRENCY_IMAGE_MAPPING = debugBlock {
            persistentMapOf<String, LocalImage<*>>()
                .builder()
                .apply {
                    put(
                        StoreCurrency.ValorantPoint.uuid,
                        LocalImage.Resource(R_ASSET_RAW.currency_vp_displayicon)
                    )
                    put(
                        StoreCurrency.KingdomCredit.uuid,
                        LocalImage.Resource(R_ASSET_RAW.currency_kc_displayicon)
                    )
                    put(
                        StoreCurrency.RadianitePoint.uuid,
                        LocalImage.Resource(R_ASSET_RAW.currency_rp_displayicon)
                    )
                }
                .build()
        }

        val WEAPON_SKIN_IDENTITY_MAPPING = debugBlock {
            persistentMapOf<String, WeaponSkinIdentity>()
                .builder()
                .apply {
                    put(
                        "dbg_wpn_skn_exc_neofrontier_melee",
                        WeaponSkinIdentity(
                            uuid = "dbg_wpn_skn_exc_neofrontier_melee",
                            displayName = ":debug: Neo Frontier Axe",
                            tier = WeaponSkinTier.EXCLUSIVE
                        )
                    )
                    put(
                        "dbg_wpn_skn_ult_spectrum_phantom",
                        WeaponSkinIdentity(
                            uuid = "dbg_wpn_skn_ult_spectrum_phantom",
                            displayName = ":debug: Spectrum Phantom",
                            tier = WeaponSkinTier.ULTRA
                        )
                    )
                    put(
                        "dbg_wpn_skn_prmm_oni_shorty",
                        WeaponSkinIdentity(
                            uuid = "dbg_wpn_skn_prmm_oni_shorty",
                            displayName = ":debug: Oni Shorty",
                            tier = WeaponSkinTier.PREMIUM
                        )
                    )
                    put(
                        "dbg_wpn_skn_dlx_sakura_stinger",
                        WeaponSkinIdentity(
                            uuid = "dbg_wpn_skn_dlx_sakura_stinger",
                            displayName = ":debug: Sakura Stinger",
                            tier = WeaponSkinTier.DELUXE
                        )
                    )
                    put(
                        "dbg_wpn_skn_slct_endeavour_bulldog",
                        WeaponSkinIdentity(
                            uuid = "dbg_wpn_skn_slct_endeavour_bulldog",
                            displayName = ":debug: Endeavour Bulldog",
                            tier = WeaponSkinTier.SELECT
                        )
                    )
                }.build()
        }

        val WEAPON_SKIN_IMAGE_MAPPING = debugBlock {
            persistentMapOf<WeaponSkinImageIdentifier, LocalImage<*>>()
                .builder()
                .apply {
                    put(
                        WeaponSkinImageIdentifier(
                            uuid = "dbg_wpn_skn_exc_neofrontier_melee",
                            type = WeaponSkinImageType.DISPLAY_SMALL
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_wpn_skn_exclusive_neofrontier_melee_displayicon)
                    )
                    put(
                        WeaponSkinImageIdentifier(
                            uuid = "dbg_wpn_skn_ult_spectrum_phantom",
                            type = WeaponSkinImageType.DISPLAY_SMALL
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_wpn_skn_ultra_spectrum_phantom_displayicon)
                    )
                    put(
                        WeaponSkinImageIdentifier(
                            uuid = "dbg_wpn_skn_prmm_oni_shorty",
                            type = WeaponSkinImageType.DISPLAY_SMALL
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_wpn_skn_premium_oni_shorty_displayicon)
                    )
                    put(
                        WeaponSkinImageIdentifier(
                            uuid = "dbg_wpn_skn_dlx_sakura_stinger",
                            type = WeaponSkinImageType.DISPLAY_SMALL
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_wpn_skn_deluxe_sakura_stringer_displayicon)
                    )
                    put(
                        WeaponSkinImageIdentifier(
                            uuid = "dbg_wpn_skn_slct_endeavour_bulldog",
                            type = WeaponSkinImageType.DISPLAY_SMALL
                        ),
                        LocalImage.Resource(R_ASSET_RAW.debug_select_endeavour_bulldog_displayicon)
                    )
                }
                .build()
        }

        val WEAPON_SKIN_TIER_IMAGE_MAPPING = debugBlock {
            persistentMapOf<String, LocalImage<*>>()
                .builder()
                .apply {
                    put(
                        WeaponSkinTier.EXCLUSIVE.uuid,
                        LocalImage.Resource(R_ASSET_RAW.contenttier_exclusive_displayicon)
                    )
                    put(
                        WeaponSkinTier.ULTRA.uuid,
                        LocalImage.Resource(R_ASSET_RAW.contenttier_ultra_displayicon)
                    )
                    put(
                        WeaponSkinTier.PREMIUM.uuid,
                        LocalImage.Resource(R_ASSET_RAW.contenttier_premium_displayicon)
                    )
                    put(
                        WeaponSkinTier.DELUXE.uuid,
                        LocalImage.Resource(R_ASSET_RAW.contenttier_deluxe_displayicon)
                    )
                    put(
                        WeaponSkinTier.SELECT.uuid,
                        LocalImage.Resource(R_ASSET_RAW.contenttier_select_displayicon)
                    )
                }
                .build()
        }

        val WEAPON_GUNBUDDY_IMAGE_MAPPING = debugBlock {
            persistentMapOf<String, LocalImage<*>>()
                .builder()
                .apply {
                }
                .build()
        }

        val TITTLE_IDENTITY_MAPPING = debugBlock {
            persistentMapOf<String, PlayerTitleIdentity>()
                .builder()
                .apply {

                }
                .build()
        }
    }
}