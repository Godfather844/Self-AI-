package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SoftLilacPrimary,
    onPrimary = Color.White,
    primaryContainer = SoftLilacLight,
    onPrimaryContainer = SoftLilacDark,
    secondary = WarmPeachPrimary,
    onSecondary = Color.White,
    secondaryContainer = WarmPeachLight,
    onSecondaryContainer = WarmPeachDark,
    tertiary = SkyBluePrimary,
    onTertiary = Color.White,
    tertiaryContainer = SkyBlueLight,
    onTertiaryContainer = SkyBlueDark,
    background = BackgroundSoft,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFCBD5E1),
    error = RoseRedPrimary,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color(0xFFF8FAFC).toArgb()
                window.navigationBarColor = Color(0xFFF8FAFC).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
