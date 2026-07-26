package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IndustrialTealLight,
    onPrimary = Color.White,
    primaryContainer = IndustrialTeal,
    onPrimaryContainer = Color.White,
    secondary = AmberGlow,
    onSecondary = Color.Black,
    secondaryContainer = AmberAccent,
    onSecondaryContainer = Color.White,
    background = SlateDarkBg,
    onBackground = DarkOnSurface,
    surface = SlateCardBg,
    onSurface = DarkOnSurface,
    surfaceVariant = SlateCardBorder,
    onSurfaceVariant = SlateTextMuted,
    outline = SlateCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = IndustrialTeal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = AmberAccent,
    onSecondary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun BunchingMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Prefer sleek dark theme for industrial app

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
