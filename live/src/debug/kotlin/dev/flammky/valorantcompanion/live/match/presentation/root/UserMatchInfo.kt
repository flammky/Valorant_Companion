package dev.flammky.valorantcompanion.live.match.presentation.root

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.flammky.valorantcompanion.assets.map.ValorantMapAsset
import dev.flammky.valorantcompanion.base.theme.material3.*
import dev.flammky.valorantcompanion.live.party.presentation.root.MockedUserPartyInfo
import dev.flammky.valorantcompanion.live.pregame.presentation.preview.TeamAgentSelectionColumnPreview
import dev.flammky.valorantcompanion.live.pvp.match.presentation.root.UserMatchInfoUIState
import dev.flammky.valorantcompanion.pvp.map.ValorantMapIdentity
import dev.flammky.valorantcompanion.assets.R as ASSET_R

@Composable
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF1A1C1E
)
private fun MatchInfoUIPreview() {
    val showAgentSelectionState = remember {
        mutableStateOf(false)
    }
    DefaultMaterial3Theme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Material3Theme.backgroundColorAsState().value)
                .statusBarsPadding()
        ) {
            Column {
                Spacer(modifier = Modifier.height(15.dp))
                MockedUserPartyInfo(padding = PaddingValues(horizontal = 15.dp))
                Spacer(modifier = Modifier.height(8.dp))
                val userMatchInfoState = remember {
                    mutableStateOf(fakeNotInMatchMatchInfoUIState())
                }
                UserMatchInfoUI(
                    state = userMatchInfoState.value,
                    refresh = {
                        userMatchInfoState.value = when(userMatchInfoState.value) {
                            fakeNotInMatchMatchInfoUIState() -> fakeInPreGameMatchInfoUIState()
                            fakeInPreGameMatchInfoUIState() -> fakeInGameMatchInfoUIState()
                            fakeInGameMatchInfoUIState() -> fakeNotInMatchMatchInfoUIState()
                            else -> userMatchInfoState.value
                        }
                    },
                    openDetail = {
                        showAgentSelectionState.value = true
                    }
                )
            }
            if (showAgentSelectionState.value) {
                BackHandler {
                    showAgentSelectionState.value = false
                }
                TeamAgentSelectionColumnPreview()
            }
        }
    }
}

private fun fakeNotInMatchMatchInfoUIState(): UserMatchInfoUIState = UserMatchInfoUIState(
    inPreGame = false,
    inGame = false,
    mapName = ValorantMapIdentity.ASCENT.display_name,
    gameModeName = "SPIKE RUSH",
    gamePodPingMs = 30,
    gamePodName = "Singapore-1",
    showLoading = false,
    errorMessage = "",
    mapId = "",
    showLoadingOnly = false,
    needManualRefresh = false,
    autoRefreshOn = false,
    userRefresh = {},
    setAutoRefresh = {}
)

private fun fakeInPreGameMatchInfoUIState(): UserMatchInfoUIState = UserMatchInfoUIState(
    inPreGame = false,
    inGame = false,
    mapName = ValorantMapIdentity.ASCENT.display_name,
    gameModeName = "SPIKE RUSH",
    gamePodPingMs = 30,
    gamePodName = "Singapore-1",
    showLoading = false,
    errorMessage = "",
    mapId = "",
    showLoadingOnly = false,
    needManualRefresh = false,
    autoRefreshOn = false,
    userRefresh = {},
    setAutoRefresh = {}
)

private fun fakeInGameMatchInfoUIState(): UserMatchInfoUIState = UserMatchInfoUIState(
    inPreGame = false,
    inGame = false,
    mapName = ValorantMapIdentity.ASCENT.display_name,
    gameModeName = "SPIKE RUSH",
    gamePodPingMs = 30,
    gamePodName = "Singapore-1",
    showLoading = false,
    errorMessage = "",
    mapId = "",
    showLoadingOnly = false,
    needManualRefresh = false,
    autoRefreshOn = false,
    userRefresh = {},
    setAutoRefresh = {}
)

@Composable
private fun UserMatchInfoUI(
    state: UserMatchInfoUIState,
    refresh: () -> Unit,
    openDetail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .background(
                color = run {
                    if (LocalIsThemeDark.current) {
                        val tint = Color.White
                        val surface = Material3Theme.surfaceColorAsState().value
                        remember(tint, surface) {
                            tint
                                .copy(alpha = 0.05f)
                                .compositeOver(surface)
                        }
                    } else {
                        Material3Theme.surfaceColorAsState().value
                    }
                }
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Match",
                color = Material3Theme.surfaceContentColorAsState().value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = refresh),
                painter = painterResource(id = ASSET_R.drawable.refresh_fill0_wght400_grad0_opsz48),
                contentDescription = "refresh",
                tint = Material3Theme.surfaceContentColorAsState().value
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            color = Material3Theme.surfaceVariantColorAsState().value
        )
        when {
            state.inPreGame -> PreGameInfoUI(
                mapName = state.mapName,
                gameModeName = state.gameModeName,
                gamePodName = state.gamePodName,
                gamePodPingMs = state.gamePodPingMs,
                openDetail = openDetail
            )
            state.inGame -> InGameInfoUI(
                mapName = state.mapName,
                gameModeName = state.gameModeName,
                gamePodName = state.gamePodName,
                gamePodPingMs = state.gamePodPingMs,
                openDetail = openDetail
            )
            else -> NotInMatchUI()
        }
    }
}

@Composable
private fun PreGameInfoUI(
    mapName: String,
    gameModeName: String,
    gamePodName: String,
    gamePodPingMs: Int,
    openDetail: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(15.dp))
        .background(Material3Theme.surfaceVariantColorAsState().value.copy(alpha = 0.80f))
        .clickable(onClick = openDetail)
        .padding(vertical = 8.dp, horizontal = 10.dp),
) {
    Text(
        text = "PRE GAME",
        color = Material3Theme.backgroundContentColorAsState().value,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = Material3Theme.surfaceVariantColorAsState().value.copy(alpha = 0.94f).compositeOver(
            Color.White)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "MAP - ${mapName.ifBlank { "Unknown Map Name" }.uppercase()}",
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = gameModeName.ifBlank { "Unknown Game Mode Name" }.uppercase(),
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (gamePodName.isNotBlank()) {
                    val pingStr = if (gamePodPingMs >= 0) "(${gamePodPingMs}ms)" else ""
                    "$gamePodName $pingStr"
                } else "",
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Icon(
            modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
            painter = painterResource(id = ASSET_R.drawable.right_arrow_100),
            contentDescription = "open detail",
            tint = Material3Theme.surfaceContentColorAsState().value
        )
    }
}

@Composable
private fun InGameInfoUI(
    mapName: String,
    gameModeName: String,
    gamePodName: String,
    gamePodPingMs: Int,
    openDetail: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(15.dp))
        .background(Material3Theme.surfaceVariantColorAsState().value.copy(alpha = 0.80f))
        .clickable(onClick = openDetail)
        .padding(vertical = 8.dp, horizontal = 10.dp),
) {
    Text(
        text = "IN GAME",
        color = Material3Theme.backgroundContentColorAsState().value,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Divider(
        modifier = Modifier.fillMaxWidth(),
        color = Material3Theme.surfaceVariantColorAsState().value.copy(alpha = 0.94f).compositeOver(
            Color.White)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "MAP - ${mapName.ifBlank { "Unknown Map Name" }.uppercase()}",
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = gameModeName.ifBlank { "Unknown Game Mode Name" }.uppercase(),
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (gamePodName.isNotBlank()) {
                    val pingStr = if (gamePodPingMs >= 0) "(${gamePodPingMs}ms)" else ""
                    "$gamePodName $pingStr"
                } else "",
                color = Material3Theme.backgroundContentColorAsState().value,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Icon(
            modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
            painter = painterResource(id = ASSET_R.drawable.right_arrow_100),
            contentDescription = "open detail",
            tint = Material3Theme.surfaceContentColorAsState().value
        )
    }
}

@Composable
private fun NotInMatchUI() = Box(
    modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 20.dp)
        .padding(vertical = 8.dp, horizontal = 10.dp)
) {
    Text(
        modifier = Modifier.align(Alignment.Center),
        text = "Not In a Match".uppercase(),
        color = Material3Theme.backgroundContentColorAsState().value.copy(alpha = 0.38f),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
    )
}