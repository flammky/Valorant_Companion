package dev.flammky.valorantcompanion.career.main

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.flammky.valorantcompanion.base.theme.material3.Material3Theme
import dev.flammky.valorantcompanion.base.theme.material3.backgroundContentColorAsState

@Composable
fun PlayerProfileCard(
    state: PlayerProfileCardState
) = PlayerProfileCardContentPlacement(
    profilePicture = { modifier ->
        ProfilePicture(
            modifier,
            state.profilePicture
        )
    },
    nameTag = { modifier ->
        NameTagText(
            modifier,
            state.riotId ?: "",
            state.tagLine ?: ""
        )
    }
)

@Composable
private fun PlayerProfileCardContentPlacement(
    profilePicture: @Composable (Modifier) -> Unit,
    nameTag: @Composable (Modifier) -> Unit,
) {
    Row(modifier = Modifier.height(56.dp)) {
        Spacer(modifier = Modifier.width(20.dp))
        profilePicture(
            Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        nameTag(
            Modifier
                .align(Alignment.CenterVertically)
                .height(50.dp)
                .weight(2f, true)
        )
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun ProfilePicture(
    modifier: Modifier,
    data: Any?
) {
    Log.d("PlayerCard", "data=$data")
    val ctx = LocalContext.current
    AsyncImage(
        modifier = modifier.clip(RoundedCornerShape(50)),
        model = remember(data, ctx) {
            ImageRequest.Builder(ctx)
                .data(data)
                .crossfade(100)
                .build()
        },
        contentDescription = "profile picture",
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun NameTagText(
    modifier: Modifier,
    name: String,
    tag: String
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            color = Material3Theme.backgroundContentColorAsState().value,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "#$tag",
            color = Material3Theme.backgroundContentColorAsState().value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}