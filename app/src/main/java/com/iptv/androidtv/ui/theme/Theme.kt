package com.iptv.androidtv.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@Composable
fun IPTVTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = AccentBlue,
        onPrimary = TextPrimary,
        secondary = AccentTeal,
        onSecondary = TextPrimary,
        background = DarkBackground,
        onBackground = TextPrimary,
        surface = DarkSurface,
        onSurface = TextPrimary,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = TextSecondary,
        error = ErrorRed,
        onError = TextPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
