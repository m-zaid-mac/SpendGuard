package com.mohammadzaid.spendguard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = RampInk,
    secondary = RampMint,
    error = RampRisk,
    background = RampSurface,
    surface = RampCard
)

private val DarkColors = darkColorScheme(
    primary = RampMint,
    secondary = RampSlate,
    error = RampRisk,
    background = RampInk,
    surface = Color(0xFF122436)
)

@Composable
fun SpendGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
