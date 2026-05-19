package com.example.nailsync.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = NailPurple,
    onPrimary = Color.White,
    primaryContainer = NailPurpleLight,
    onPrimaryContainer = Color.White,
    secondary = NailPurpleMid,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = NailPurpleDark,
    tertiary = NailTeal,
    onTertiary = Color.White,
    tertiaryContainer = NailTealDark,
    onTertiaryContainer = Color.White,
    background = Color(0xFFF5F0FF),
    onBackground = Color(0xFF1A1030),
    surface = Color.White,
    onSurface = Color(0xFF1A1030),
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = Color(0xFF4A3B6E),
    error = StatusCancelled,
    onError = Color.White
)

@Composable
fun NailSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
