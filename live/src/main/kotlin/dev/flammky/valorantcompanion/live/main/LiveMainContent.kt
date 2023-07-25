package dev.flammky.valorantcompanion.live.main

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.flammky.valorantcompanion.base.compose.lazy.LazyContent
import dev.flammky.valorantcompanion.base.theme.material3.material3Background
import dev.flammky.valorantcompanion.live.loadout.presentation.root.LiveLoadout
import dev.flammky.valorantcompanion.live.pvp.presentation.LivePVP
import dev.flammky.valorantcompanion.live.store.presentation.root.LiveStore

@Composable
internal fun LiveMainContent() {
    val selectedTabIndex = remember {
        mutableStateOf(0)
    }
    val container = remember {
        object {
            private val visibleScreenState = mutableStateListOf<Pair<String, @Composable () -> Unit>>()

            fun dismiss(key: String) {
                visibleScreenState.removeAll { it.first == key }
            }

            fun show(key: String, content: @Composable LiveMainScreenContainer.() -> Unit) {
                dismiss(key)
                visibleScreenState.add(
                    key to @Composable {
                        remember {
                            object : LiveMainScreenContainer {
                                override val isVisible: Boolean
                                    get() = visibleScreenState.lastOrNull()?.first == key
                                override fun dismiss() {
                                    dismiss(key)
                                }
                            }
                        }.content()
                    }
                )
            }

            @Composable
            fun Content() {
                if (visibleScreenState.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {}
                            .material3Background()
                    ) {
                        visibleScreenState.forEach { screen -> screen.second.invoke() }
                    }
                }
            }
        }
    }
    LiveMainContentPlacement(
        topTab = {
            LiveMainContentTopTab(
                modifier = Modifier.padding(top = 12.dp),
                selectedTabIndex = selectedTabIndex.value,
                selectTab = { selectedTabIndex.value = it },
            )
        },
        topTabContent = {
            LazyContent(trigger = selectedTabIndex.value == 0) {
                LivePVP(
                    modifier = Modifier
                        .zIndex(if (selectedTabIndex.value == 0) 1f else 0f),
                    openScreen = { content ->
                        val key = 0.toString()
                        container.show(
                            key = key,
                            content = content
                        )
                    }
                )
            }

            LazyContent(trigger = selectedTabIndex.value == 1) {
                LiveLoadout(
                    modifier = Modifier
                        .zIndex(if (selectedTabIndex.value == 1) 1f else 0f),
                    openScreen = { content ->
                        val key = 1.toString()
                        container.show(
                            key = key,
                            content = content
                        )
                    }
                )
            }

            LazyContent(trigger = selectedTabIndex.value == 2) {
                LiveStore(
                    modifier = Modifier
                        .zIndex(if (selectedTabIndex.value == 2) 1f else 0f),
                    openScreen = { content ->
                        val key = 2.toString()
                        container.show(
                            key = key,
                            content = content
                        )
                    }
                )
            }
        },
        screenContainer = {
            container.Content()
        }
    )
}

@Composable
internal inline fun LiveMainContentPlacement(
    topTab: @Composable () -> Unit,
    topTabContent: @Composable () -> Unit,
    screenContainer: @Composable () -> Unit,
    // TODO: define navigation bars inset
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(bottom = 80.dp)
) {
    Column() {
        Box { topTab() }
        Box { topTabContent() }
    }
    screenContainer()
}