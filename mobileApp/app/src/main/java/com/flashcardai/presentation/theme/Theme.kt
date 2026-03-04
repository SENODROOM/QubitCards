package com.flashcardai.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Colors ───────────────────────────────────────────────────────────────

val Purple100 = Color(0xFFEDE7F6)
val Purple200 = Color(0xFFCE93D8)
val Purple400 = Color(0xFFAB47BC)
val Purple600 = Color(0xFF7B1FA2)
val Purple700 = Color(0xFF6A1B9A)
val Purple900 = Color(0xFF4A148C)

val DeepPurple = Color(0xFF6C63FF)
val Coral = Color(0xFFFF6584)
val Teal = Color(0xFF26C6DA)
val Amber = Color(0xFFFFCA28)
val Green = Color(0xFF66BB6A)

val SurfaceLight = Color(0xFFF8F4FF)
val SurfaceDark = Color(0xFF1A1625)
val CardLight = Color(0xFFFFFFFF)
val CardDark = Color(0xFF252136)
val OnSurfaceLight = Color(0xFF1A1A2E)
val OnSurfaceDark = Color(0xFFF0ECFF)

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    onPrimary = Color.White,
    primaryContainer = Purple100,
    onPrimaryContainer = Purple900,
    secondary = Coral,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE4EC),
    onSecondaryContainer = Color(0xFF8B1A34),
    tertiary = Teal,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = CardLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFEEE8FF),
    onSurfaceVariant = Color(0xFF4A4458),
    outline = Color(0xFFCCC5D9),
    error = Color(0xFFE53935),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB39DFF),
    onPrimary = Color(0xFF290074),
    primaryContainer = Purple700,
    onPrimaryContainer = Purple100,
    secondary = Coral,
    onSecondary = Color(0xFF5C0020),
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = CardDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = Color(0xFF2D2840),
    onSurfaceVariant = Color(0xFFCCC5D9),
    outline = Color(0xFF4A4458),
)

@Composable
fun FlashCardAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
