package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFF9CF4FF),
    secondary = NeonPurple,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3E2D68),
    onSecondaryContainer = Color(0xFFE9DDFF),
    tertiary = NeonAmber,
    onTertiary = Color.Black,
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF3C3C54)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek studio dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
