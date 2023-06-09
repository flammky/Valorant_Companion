package dev.flammky.valorantcompanion.live.ingame.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.flammky.valorantcompanion.base.theme.material3.LocalIsThemeDark
import dev.flammky.valorantcompanion.base.theme.material3.Material3Theme
import dev.flammky.valorantcompanion.base.theme.material3.surfaceColorAsState
import dev.flammky.valorantcompanion.assets.LocalImage

@Composable
internal fun LiveInGameTeamMemberCard(
    modifier: Modifier,
    state: LiveInGameTeamMemberCardState
) = LiveInGameTeamMemberCardPlacement(
    modifier = modifier,
    surface = {
        Box(it.fillMaxSize().background(Material3Theme.surfaceColorAsState().value))
    },
    agentPicture = {
        LiveinGameTeamMemberCardAgentPicture(
            modifier = it,
            key = state.agentIconKey,
            res = state.agentIcon
        )
    },
    title = {
        LiveInGameTeamMemberCardTitle(
            modifier = it,
            username = state.username,
            tagline = state.tagline,
            isUser = state.isUser
        )
    },
    subtitle = {
        LiveInGameTeamMemberCardSubtitle(
            modifier = it,
            agentName = state.agentName,
            roleName = state.roleName,
            roleIcon = state.roleIcon,
            roleIconKey = state.roleIconKey
        )
    },
    competitiveTierIcon = {
        LiveInGameTeamMemberCardCompetitiveTierIcon(
            modifier = it,
            res = state.competitiveTierIcon,
            resKey = state.competitiveTierIconKey
        )
    }
)

@Composable
private fun LiveInGameTeamMemberCardPlacement(
    modifier: Modifier,
    surface: @Composable (Modifier) -> Unit,
    agentPicture: @Composable (Modifier) -> Unit,
    title: @Composable (Modifier) -> Unit,
    subtitle: @Composable (Modifier) -> Unit,
    competitiveTierIcon: @Composable (Modifier) -> Unit
) = Box(modifier = modifier.height(42.dp)) {
    surface(Modifier)
    Row(Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
        agentPicture(Modifier.align(Alignment.CenterVertically))
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(2f, true)
        ) {
            Row(modifier = Modifier.weight(1f)) {
                title(Modifier.align(Alignment.CenterVertically))
            }
            Row(modifier = Modifier.weight(1f)) {
                subtitle(Modifier.align(Alignment.CenterVertically))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        competitiveTierIcon(
            Modifier
                .fillMaxHeight()
                .aspectRatio(1f, true)
        )
    }
}

@Composable
private fun LiveinGameTeamMemberCardAgentPicture(
    modifier: Modifier,
    key: Any,
    res: LocalImage<*>?
) {
    val ctx = LocalContext.current
    AsyncImage(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(1f, true)
            .clip(RoundedCornerShape(50))
            .border(1.dp, Color(0xFF60c18e), RoundedCornerShape(50)),
        model = remember(key) {
            ImageRequest.Builder(ctx)
                .setParameter("key", key, null)
                .data(res?.value)
                .build()
        },
        contentDescription = null
    )
}

@Composable
private fun LiveInGameTeamMemberCardTitle(
    modifier: Modifier,
    username: String?,
    tagline: String?,
    isUser: Boolean
) {
    val textColor =
        if (LocalIsThemeDark.current) Color.White else Color.Black
    val textStyle =
        MaterialTheme.typography.titleSmall
    val labelTextStyle =
        MaterialTheme.typography.labelMedium
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (username != null) {
            Text(
                text = username,
                color = textColor,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // should we hide this when incognito is enabled ?
        if (tagline != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .background(textColor.copy(alpha = 0.1f))
                    .padding(horizontal = 2.dp)
            ) {
                Text(text = "#$tagline", color = textColor, style = labelTextStyle)
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .background(textColor.copy(alpha = 0.1f))
                    .padding(horizontal = 2.dp)
            ) {
                Text(text = "me", color = textColor, style = labelTextStyle)
            }
        }
    }
}

@Composable
private fun LiveInGameTeamMemberCardSubtitle(
    modifier: Modifier,
    agentName: String?,
    roleName: String?,
    roleIcon: LocalImage<*>?,
    roleIconKey: Any,
) {
    val ctx = LocalContext.current
    val textColor =
        if (LocalIsThemeDark.current) Color.White else Color.Black
    Row(modifier = modifier) {
        // maybe add && if (roleIcon is LocalImageData.Resouce) rolceIcon.value != 0 else true
        if (roleIcon != null) {
            AsyncImage(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterVertically),
                model = remember(roleIconKey) {
                    ImageRequest.Builder(ctx)
                        .run {
                            data(roleIcon.value)
                        }
                        .build()
                },
                colorFilter = ColorFilter.tint(if (LocalIsThemeDark.current) Color.White else Color.Black),
                contentDescription = roleName?.let { "$roleName role Icon" }
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        if (agentName != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                text = agentName,
                color = textColor,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LiveInGameTeamMemberCardCompetitiveTierIcon(
    modifier: Modifier,
    res: LocalImage<*>?,
    resKey: Any
) {
    val ctx = LocalContext.current
    AsyncImage(
        modifier = modifier
            .fillMaxSize(),
        model = remember(resKey) {
            ImageRequest.Builder(ctx)
                .setParameter("key", resKey, null)
                .data(res?.value)
                .build()
        },
        contentDescription = null
    )
}