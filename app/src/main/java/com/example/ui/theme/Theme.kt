package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0E3A4A),
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = HighTechBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0F3652),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = AccentPurple,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3B1F69),
    onTertiaryContainer = Color(0xFFDDD6FE),
    background = CommandDarkBg,
    onBackground = TextPrimary,
    surface = CommandSurface,
    onSurface = TextPrimary,
    surfaceVariant = CommandSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = CriticalRed,
    onError = Color.White
)

@Composable
fun ShelterOpsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
