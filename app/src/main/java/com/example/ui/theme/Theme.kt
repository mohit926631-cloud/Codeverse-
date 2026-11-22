package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val CodeverseDarkColorScheme = darkColorScheme(
    primary = CyberPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3A2775),
    onPrimaryContainer = NeonLavender,
    secondary = CyberCyan,
    onSecondary = DeepNavy,
    secondaryContainer = Color(0xFF143D52),
    onSecondaryContainer = Color(0xFF9CEBFF),
    tertiary = NeonLavender,
    onTertiary = DeepNavy,
    tertiaryContainer = Color(0xFF362C5B),
    onTertiaryContainer = Color(0xFFECE2FF),
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = SpaceVoid,
    surfaceContainerLow = DeepNavy,
    surfaceContainer = CardNavy,
    surfaceContainerHigh = MidnightNavy,
    surfaceContainerHighest = ElevatedNavy,
    outline = OutlineNavy,
    outlineVariant = Color(0xFF2D3158),
    error = CoralError,
    onError = Color.White,
    errorContainer = CoralErrorContainer,
    onErrorContainer = Color(0xFFFFC1CE)
)

private val CodeverseLightColorScheme = lightColorScheme(
    primary = CyberPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECE3FF),
    onPrimaryContainer = Color(0xFF2C1068),
    secondary = CyberCyanDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6F5FF),
    onSecondaryContainer = Color(0xFF003546),
    tertiary = Color(0xFF6B48C4),
    onTertiary = Color.White,
    background = Color(0xFFF5F6FC),
    onBackground = DeepNavy,
    surface = Color.White,
    onSurface = DeepNavy,
    surfaceVariant = Color(0xFFE8EBF8),
    onSurfaceVariant = Color(0xFF474B73),
    outline = Color(0xFFC8CDE6),
    error = CoralError,
    onError = Color.White
)

val CodeverseShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun CodeverseTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CodeverseDarkColorScheme else CodeverseLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CodeverseTypography,
        shapes = CodeverseShapes,
        content = content
    )
}
