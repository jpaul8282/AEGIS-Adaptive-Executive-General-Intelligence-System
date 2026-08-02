package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AegisGoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = AegisGoldContainer,
    onPrimaryContainer = AegisGoldPrimary,
    secondary = AegisCyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = AegisCyanContainer,
    onSecondaryContainer = AegisCyanAccent,
    tertiary = AegisSecurityGreen,
    background = AegisDarkCanvas,
    onBackground = AegisTextPrimary,
    surface = AegisSurfaceDark,
    onSurface = AegisTextPrimary,
    surfaceVariant = AegisSurfaceVariantDark,
    onSurfaceVariant = AegisTextSecondary,
    outline = AegisBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = AegisGoldPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = AegisGoldPrimaryLight,
    secondary = AegisCyanAccentLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = AegisCyanAccentLight,
    tertiary = Color(0xFF059669),
    background = AegisLightCanvas,
    onBackground = Color(0xFF0F172A),
    surface = AegisSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AegisSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = AegisBorderLight
)

@Composable
fun AegisTheme(
    darkTheme: Boolean = true, // Default to AEGIS Dark Tactical Canvas
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
