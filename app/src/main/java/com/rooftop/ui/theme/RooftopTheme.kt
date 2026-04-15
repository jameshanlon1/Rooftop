package com.rooftop.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RooftopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = RooftopPrimary,
            onPrimary = RooftopOnPrimary,
            background = RooftopBackground,
            surface = RooftopSurface,
            surfaceVariant = RooftopSurfaceVariant,
            onSurface = RooftopOnSurface,
            onSurfaceVariant = RooftopOnSurfaceVariant,
            error = RooftopError
        ),
        content = content
    )
}
