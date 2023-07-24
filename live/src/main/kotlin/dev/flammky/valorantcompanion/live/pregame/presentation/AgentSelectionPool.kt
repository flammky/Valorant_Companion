package dev.flammky.valorantcompanion.live.pregame.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.flammky.valorantcompanion.assets.R
import dev.flammky.valorantcompanion.base.rememberThis
import dev.flammky.valorantcompanion.assets.LocalImage
import dev.flammky.valorantcompanion.pvp.agent.ValorantAgent
import dev.flammky.valorantcompanion.pvp.agent.ValorantAgentIdentity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentSelectionPool(
    modifier: Modifier,
    // list of agent Id's that the player has unlocked
    unlockedAgents: List<String>,
    // list of agent Id's that is locked by teammates or other reason
    disabledAgents: List<String>,
    // list of available agent within the pool
    agentPool: List<ValorantAgent>,
    selectedAgent: String?,
    onSelected: (String) -> Unit
) {
    val upOnSelected = rememberUpdatedState(newValue = onSelected)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val unlockedInPool = mutableListOf<@Composable () -> Unit>()
        val notUnlockedInPool = mutableListOf<@Composable () -> Unit>()
        agentPool.forEach { agent ->
            val identity = ValorantAgentIdentity.of(agent)
            val userAgent = identity.uuid == selectedAgent
            val unlocked = unlockedAgents.any { it == identity.uuid }
            val disabled = disabledAgents.any { it == identity.uuid }
            val block = @Composable {
                SelectableAgentIcon(
                    modifier = Modifier.size(45.dp),
                    enabled = !userAgent && unlocked && !disabled,
                    locked = !unlocked,
                    // TODO: should get the icon via AssetLoader
                    data = LocalImage.Resource(agentIconOf(agent)),
                    dataKey = agent,
                    agentName = identity.displayName,
                    onSelected = { upOnSelected.value(identity.uuid) }
                )
            }
            if (unlocked) unlockedInPool.add(block) else notUnlockedInPool.add(block)
        }
        unlockedInPool.forEach { composable -> composable.invoke() }
        notUnlockedInPool.forEach { composable -> composable.invoke() }
    }
}

@Composable
private fun SelectableAgentIcon(
    modifier: Modifier,
    enabled: Boolean,
    locked: Boolean,
    data: LocalImage<*>?,
    dataKey: Any,
    agentName: String,
    onSelected: () -> Unit
) {
    val hash = remember {
        mutableStateOf(0)
    }.rememberThis(dataKey) {
        value++
    }
    val ctx = LocalContext.current
    val inspection = LocalInspectionMode.current
    Box(
        modifier = Modifier.border(
            1.dp,
            Color.White.copy(alpha = 0.5f),
        )
    ) {
        Box(
            modifier = modifier
                .rememberThis(enabled) {
                    if (enabled) {
                        clickable(
                            enabled = true,
                            onClick = onSelected,
                        )
                    } else {
                        alpha(0.38f)
                    }
                }
        ) {
            AsyncImage(
                model = remember(hash, inspection) {
                    ImageRequest.Builder(ctx)
                        .setParameter("retry_hash", hash)
                        .run {
                            if (inspection && data is LocalImage.Resource) {
                                placeholder(data.value)
                            } else {
                                data(data?.value)
                            }
                        }
                        .build()
                },
                contentDescription = "Agent Icon of $agentName"
            )
        }
        if (locked) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),
                painter = painterResource(id = R.drawable.lock_fill0_wght400_grad0_opsz48),
                contentDescription = "Locked Agent Icon of $agentName",
                tint = Color.White
            )
        }
    }
}

@Deprecated("use AssetLoaderService instead")
private fun agentIconOf(agent: ValorantAgent): Int {
    return when(agent) {
        ValorantAgent.ASTRA -> R.raw.agent_astra_displayicon
        ValorantAgent.BREACH -> R.raw.agent_breach_displayicon
        ValorantAgent.BRIMSTONE -> R.raw.agent_brimstone_displayicon
        ValorantAgent.CHAMBER -> R.raw.agent_chamber_displayicon
        ValorantAgent.CYPHER -> R.raw.agent_cypher_displayicon
        ValorantAgent.FADE -> R.raw.agent_fade_displayicon
        ValorantAgent.GEKKO -> R.raw.agent_gekko_displayicon
        ValorantAgent.HARBOR -> R.raw.agent_harbor_displayicon
        ValorantAgent.JETT -> R.raw.agent_jett_displayicon
        ValorantAgent.KAYO -> R.raw.agent_kayo_displayicon
        ValorantAgent.KILLJOY -> R.raw.agent_killjoy_displayicon
        ValorantAgent.NEON -> R.raw.agent_neon_displayicon
        ValorantAgent.OMEN -> R.raw.agent_omen_displayicon
        ValorantAgent.PHOENIX -> R.raw.agent_phoenix_displayicon
        ValorantAgent.RAZE -> R.raw.agent_raze_displayicon
        ValorantAgent.REYNA -> R.raw.agent_reyna_displayicon
        ValorantAgent.SAGE -> R.raw.agent_sage_displayicon
        ValorantAgent.SKYE -> R.raw.agent_skye_displayicon
        ValorantAgent.SOVA -> R.raw.agent_sova_displayicon
        ValorantAgent.VIPER -> R.raw.agent_viper_displayicon
        ValorantAgent.YORU -> R.raw.agent_yoru_displayicon
    }
}