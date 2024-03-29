package dev.flammky.valorantcompanion.boarding.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.flammky.valorantcompanion.base.rememberThis
import dev.flammky.valorantcompanion.base.theme.material3.Material3Theme
import dev.flammky.valorantcompanion.base.theme.material3.backgroundColorAsState

@Composable
fun LoginLoadingScreen(
    modifier: Modifier,
    show: Boolean
) {
    if (show) {
        val backgroundColor = Material3Theme.backgroundColorAsState().value
        Box(
            modifier = remember {
                modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {}
            }.rememberThis(backgroundColor) {
                background(backgroundColor)
            }
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(65.dp)
                    .align(Alignment.Center),
                color = Color.Red
            )
        }
    }
}