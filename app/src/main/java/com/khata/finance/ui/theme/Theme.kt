package com.khata.finance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GreenPrimary = Color(0xFF1E8E5A)
private val GreenPrimaryDark = Color(0xFF4CD397)
private val LightColors = lightColorScheme(primary = GreenPrimary, secondary = Color(0xFF00695C))
private val DarkColors = darkColorScheme(primary = GreenPrimaryDark, secondary = Color(0xFF4DB6AC))

@Composable
fun KhataTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
