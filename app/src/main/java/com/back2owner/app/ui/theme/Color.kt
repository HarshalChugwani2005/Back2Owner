package com.back2owner.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Campus Blue and Safety Orange color palette
val CampusBlue = Color(0xFF0052CC)
val CampusBlueLight = Color(0xFF3D7FD7)
val CampusBlueDark = Color(0xFF00358A)
val SafetyOrange = Color(0xFFFF6B35)
val SafetyOrangeLight = Color(0xFFFF8855)
val SafetyOrangeDark = Color(0xFFC94F2F)

val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

val SuccessGreen = Color(0xFF28A745)
val ErrorRed = Color(0xFFDC3545)
val WarningYellow = Color(0xFFFFC107)
val InfoBlue = Color(0xFF17A2B8)

private val LightColorScheme = lightColorScheme(
    primary = CampusBlue,
    onPrimary = Color.White,
    primaryContainer = CampusBlueLight,
    onPrimaryContainer = Color.White,
    secondary = SafetyOrange,
    onSecondary = Color.White,
    secondaryContainer = SafetyOrangeLight,
    onSecondaryContainer = Color.White,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    error = ErrorRed,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F),
    errorContainer = Color(0xFFFFDADA),
    onErrorContainer = ErrorRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = CampusBlueLight,
    onPrimary = CampusBlueDark,
    primaryContainer = CampusBlueDark,
    onPrimaryContainer = CampusBlueLight,
    secondary = SafetyOrangeLight,
    onSecondary = SafetyOrangeDark,
    secondaryContainer = SafetyOrangeDark,
    onSecondaryContainer = SafetyOrangeLight,
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    error = Color(0xFFFF6B6B),
    onError = ErrorRed,
    background = BackgroundDark,
    onBackground = Color(0xFFE0E0E0),
    surface = SurfaceDark,
    onSurface = Color(0xFFE0E0E0),
    errorContainer = Color(0xFF5F2C2C),
    onErrorContainer = Color(0xFFFF6B6B),
)

@Composable
fun Back2OwnerTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Back2OwnerTypography,
        shapes = Back2OwnerShapes,
        content = content,
    )
}
