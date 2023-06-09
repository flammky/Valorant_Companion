package dev.flammky.valorantcompanion.live

import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.flammky.valorantcompanion.live.ingame.presentation.LiveInGame
import dev.flammky.valorantcompanion.live.match.presentation.root.LiveMatchUI
import dev.flammky.valorantcompanion.live.party.presentation.LivePartyUI
import dev.flammky.valorantcompanion.live.pregame.presentation.LivePreGame
import dev.flammky.valorantcompanion.live.pregame.presentation.rememberLivePreGamePresenter

@Composable
fun LiveMain() {

    val showPreGameScreen = remember {
        mutableStateOf(false)
    }

    val showInGameScreen = remember {
        mutableStateOf(false)
    }

    LiveMainPlacement(
        showMatchDetail = showPreGameScreen.value || showInGameScreen.value,
        liveParty = {
            LivePartyUI(modifier = Modifier)
        },
        liveMatch = {
            LiveMatchUI(
                modifier = Modifier,
                openPreGameDetail = { showPreGameScreen.value = true },
                openInGameDetail = { showInGameScreen.value = true }
            )
        },
        matchDetail = {
            // TODO: define LocalNavigationBarInsets
            if (showPreGameScreen.value) {
                LivePreGame(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(bottom = 80.dp),
                    dismiss = { showPreGameScreen.value = false }
                )
            } else if (showInGameScreen.value) {
                LiveInGame(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(bottom = 80.dp),
                    dismiss = { showInGameScreen.value = false }
                )
            }
        },
    )
}

@Composable
private fun LiveMainPlacement(
    showMatchDetail: Boolean,
    liveParty: @Composable () -> Unit,
    liveMatch: @Composable () -> Unit,
    matchDetail: @Composable () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .padding(
                    top = with(LocalDensity.current) {
                        WindowInsets.statusBars.getTop(this).toDp()
                    }
                )
        ) {
            liveParty()
            liveMatch()
        }
        if (showMatchDetail) matchDetail()
    }
}