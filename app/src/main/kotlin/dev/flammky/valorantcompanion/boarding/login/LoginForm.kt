@file:OptIn(ExperimentalMaterial3Api::class)

package dev.flammky.valorantcompanion.boarding.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.flammky.valorantcompanion.R
import dev.flammky.valorantcompanion.base.runRemember
import dev.flammky.valorantcompanion.base.theme.material3.LocalIsThemeDark
import dev.flammky.valorantcompanion.base.theme.material3.Material3Theme
import dev.flammky.valorantcompanion.base.theme.material3.backgroundColorAsState
import dev.flammky.valorantcompanion.base.theme.material3.backgroundContentColorAsState

@Composable
fun LoginForm(
    modifier: Modifier,
    state: LoginFormState
) {
    val slot = state.inputSlot
    LoginFormPlacements(
        modifier = modifier,
        textFields = { modifier ->
            LoginFormTextFields(
                modifier,
                inputSlot = slot
            )
        },
        loginButton = { modifier ->
            LoginFormButton(
                modifier,
                state.canLogin,
                onClick = state::login
            )
        }
    )
}

@Composable
private fun LoginFormPlacements(
    modifier: Modifier,
    textFields: @Composable (Modifier) -> Unit,
    loginButton: @Composable (Modifier) -> Unit
) = Column(modifier = modifier.fillMaxSize()) {
    Spacer(modifier = Modifier.height(100.dp))
    textFields(Modifier.align(Alignment.CenterHorizontally))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        loginButton(Modifier.align(Alignment.CenterHorizontally))
        Spacer(
            modifier = Modifier.height(
                maxOf(
                    100.dp,
                    with(LocalDensity.current) {
                        WindowInsets.ime.getBottom(this).toDp().also { Log.d("LoginScreen", "ime height=$it") }
                    } + 20.dp
                )
            )
        )
    }
}

@Composable
private fun LoginFormTextFields(
    modifier: Modifier,
    inputSlot: LoginFormInputSlot
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Sign in",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Material3Theme.backgroundContentColorAsState().value
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.8f),
            text = inputSlot.exceptionMessage,
            style = MaterialTheme.typography.labelMedium,
            color = remember { Color(0xFFBE29CC) },
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        UsernameTextField(
            value = inputSlot.username,
            onValueChange = inputSlot::usernameInput
        )
        Spacer(modifier = Modifier.height(15.dp))
        PasswordTextField(
            value = inputSlot.password,
            onValueChange = inputSlot::passwordInput,
            !inputSlot.visiblePassword,
            inputSlot::toggleVisiblePassword
        )
    }
}

@Composable
private fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val hasFocusState = remember {
        mutableStateOf(false)
    }
    val textColor = Material3Theme.backgroundContentColorAsState().value
    val outlineColor = Material3Theme.backgroundContentColorAsState().value
    TextField(
        modifier = remember {
            Modifier
                .fillMaxWidth(0.8f)
                .onFocusChanged { state ->
                    hasFocusState.value = state.isFocused
                }
        }.runRemember(hasFocusState.value, outlineColor) {
            if (hasFocusState.value) border(width = 2.dp, color = outlineColor, shape = RoundedCornerShape(10)) else this
        },
        textStyle = MaterialTheme.typography.labelLarge.runRemember {
            copy(color = textColor, fontWeight = FontWeight.SemiBold)
        },
        value = value,
        label = {
            Text(
                "USERNAME",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Material3Theme.backgroundContentColorAsState()
                    .value
                    .copy(alpha = 0.65f)
                    .compositeOver(Material3Theme.backgroundColorAsState().value)
            )
        },
        onValueChange = onValueChange,
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = if (hasFocusState.value) {
                Color.Transparent
            } else {
                if (LocalIsThemeDark.current) {
                    remember { Color(0xFF0D0D0D) }
                } else {
                    remember { Color(0xFFEDEDED) }
                }
            },
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    mask: Boolean,
    toggleMask: () -> Unit
) {
    val maskInteractionSource = remember {
        MutableInteractionSource()
    }
    val hasFocusState = remember {
        mutableStateOf(false)
    }
    val textColor = Material3Theme.backgroundContentColorAsState().value
    val outlineColor = Material3Theme.backgroundContentColorAsState().value
    TextField(
        modifier = remember {
            Modifier
                .fillMaxWidth(0.8f)
                .onFocusChanged { state ->
                    hasFocusState.value = state.isFocused
                }
        }.runRemember(hasFocusState.value, outlineColor) {
            if (hasFocusState.value) border(width = 2.dp, color = outlineColor, shape = RoundedCornerShape(10)) else this
        },
        textStyle = MaterialTheme.typography.labelLarge.runRemember {
            copy(color = textColor, fontWeight = FontWeight.SemiBold)
        },
        value = value,
        label = {
            Text(
                "PASSWORD",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Material3Theme.backgroundContentColorAsState()
                    .value
                    .copy(alpha = 0.65f)
                    .compositeOver(Material3Theme.backgroundColorAsState().value)
            )
        },
        trailingIcon = {
            if (!hasFocusState.value) {
                return@TextField
            }
            Icon(
                modifier = remember {
                    Modifier
                        .size(24.dp)
                }.runRemember(toggleMask) {
                    clickable(
                        interactionSource = maskInteractionSource,
                        indication = null,
                        onClick = toggleMask
                    )
                },
                painter = rememberVectorPainter(
                    image = ImageVector.vectorResource(
                        id = if (mask) { R.drawable.visibility_off_48px } else R.drawable.visibility_on_48px
                    )
                ),
                contentDescription = "mask",
                tint = Material3Theme.backgroundContentColorAsState().value
            )
        },
        onValueChange = onValueChange,
        maxLines = 1,
        singleLine = true,
        visualTransformation = if (mask) {
            remember { PasswordVisualTransformation() }
        } else {
            VisualTransformation.None
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = if (hasFocusState.value) {
                Color.Transparent
            } else {
                if (LocalIsThemeDark.current) {
                    remember { Color(0xFF0D0D0D) }
                } else {
                    remember { Color(0xFFEDEDED) }
                }
            },
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun LoginFormButton(
    modifier: Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (enabled) {
        Color.Red
    } else {
        Material3Theme.backgroundContentColorAsState().value.copy(alpha = 0.2f)
    }
    val iconTint = if (enabled) {
        Color.White
    } else {
        Material3Theme.backgroundContentColorAsState().value.copy(alpha = 0.2f)
    }
    val backgroundColor = if (enabled) {
        Color.Red
    } else {
        Color.Transparent
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Box(
        modifier = remember {
            modifier
                .size(65.dp)
                .clip(shape = RoundedCornerShape(25))
        }.runRemember(backgroundColor) {
            background(color = backgroundColor)
        }.runRemember(borderColor) {
            border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(25))
        }.runRemember(enabled, onClick) {
            clickable(
                enabled = enabled,
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource
            )
        }
    ) {
        Icon(
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.Center),
            painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.right_arrow_100dp)),
            contentDescription = "Log In",
            tint = iconTint
        )
    }
}