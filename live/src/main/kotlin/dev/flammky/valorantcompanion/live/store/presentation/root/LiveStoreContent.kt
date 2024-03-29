package dev.flammky.valorantcompanion.live.store.presentation.root

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.flammky.valorantcompanion.base.compose.clickable
import dev.flammky.valorantcompanion.base.theme.material3.interactiveUiElementTextAlphaEnforcement
import dev.flammky.valorantcompanion.base.theme.material3.interactiveUiElementAlphaEnforcement
import dev.flammky.valorantcompanion.base.theme.material3.interactiveUiElementSizeEnforcement

@Composable
internal fun LiveStoreContent(
    modifier: Modifier,
    dailyOfferEnabled: Boolean,
    openDailyOffer: () -> Unit,
    nightMarketOpen: Boolean,
    openNightMarket: () -> Unit,
    agentEnabled: Boolean,
    openAgent: () -> Unit
) {
    BoxWithConstraints {
        val maxWidth = maxWidth
        Column(modifier) {
            Row {
                OpenDailyOfferCard(
                    modifier = Modifier
                        .size(maxWidth / 2 - 8.dp)
                        .padding(start = 16.dp, top = 16.dp)
                        .interactiveUiElementSizeEnforcement()
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = false,
                            enabled = dailyOfferEnabled
                        )
                        .clickable(enabled = dailyOfferEnabled, onClick = openDailyOffer),
                    iconModifier = Modifier
                        .interactiveUiElementAlphaEnforcement(
                            isContent = true,
                            enabled = dailyOfferEnabled
                        ),
                    textModifier = Modifier
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = true,
                            enabled = dailyOfferEnabled
                        )
                )
                Spacer(modifier = Modifier.width(16.dp))
                OpenNightMarketOfferCard(
                    modifier = Modifier
                        .size(maxWidth / 2 - 8.dp)
                        .padding(end = 16.dp, top = 16.dp)
                        .interactiveUiElementSizeEnforcement()
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = false,
                            enabled = nightMarketOpen
                        )
                        .clickable(enabled = nightMarketOpen, onClick = openNightMarket),
                    iconModifier = Modifier
                        .interactiveUiElementAlphaEnforcement(
                            isContent = true,
                            enabled = nightMarketOpen
                        ),
                    textModifier = Modifier
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = true,
                            enabled = nightMarketOpen
                        )
                )
            }
            Row {
                OpenAgentOfferCard(
                    modifier = Modifier
                        .size(maxWidth / 2 - 8.dp)
                        .padding(start = 16.dp, top = 16.dp)
                        .interactiveUiElementSizeEnforcement()
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = false,
                            enabled = agentEnabled
                        )
                        .clickable(agentEnabled, onClick = openAgent),
                    iconModifier = Modifier
                        .interactiveUiElementAlphaEnforcement(
                            isContent = true,
                            enabled = agentEnabled
                        ),
                    textModifier = Modifier
                        .interactiveUiElementTextAlphaEnforcement(
                            isContent = true,
                            enabled = agentEnabled
                        )
                )
            }
        }
    }
}