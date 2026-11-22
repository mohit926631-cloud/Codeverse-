package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary CODEVERSE brand palette
val MidnightNavy = Color(0xFF252747)
val DeepNavy = Color(0xFF191B30)
val SpaceVoid = Color(0xFF111325)
val CardNavy = Color(0xFF202342)
val ElevatedNavy = Color(0xFF2D3059)
val OutlineNavy = Color(0xFF3E4274)

val CyberPurple = Color(0xFF8050E8)
val CyberPurpleDark = Color(0xFF5A2EC2)
val CyberCyan = Color(0xFF22C5E9)
val CyberCyanDark = Color(0xFF0E8EAD)
val NeonLavender = Color(0xFFB89AFF)

// Semantic status colors
val SoftGreen = Color(0xFF38E59D)
val SoftGreenContainer = Color(0xFF143C37)
val WarmYellow = Color(0xFFFFCA57)
val WarmYellowContainer = Color(0xFF3F331D)
val CoralError = Color(0xFFFF5C7C)
val CoralErrorContainer = Color(0xFF431E2D)

// Text hierarchy
val TextPrimary = Color(0xFFF6F7FF)
val TextSecondary = Color(0xFFB5B9D6)
val TextMuted = Color(0xFF7D82A8)

// Code syntax colors (always high-contrast on DeepNavy)
val SyntaxKeyword = Color(0xFFC792EA)
val SyntaxString = Color(0xFF7CE8A4)
val SyntaxTag = Color(0xFF42D6F5)
val SyntaxAttr = Color(0xFFFFCB6B)
val SyntaxComment = Color(0xFF6B739E)
val SyntaxNumber = Color(0xFFFF8A65)

// Signature reusable brushes
val PrimaryAccentGradient = Brush.horizontalGradient(
    colors = listOf(CyberPurple, CyberCyan)
)

val HeroCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF312867), Color(0xFF21264D), Color(0xFF183652))
)

val DeepSpaceGradient = Brush.verticalGradient(
    colors = listOf(DeepNavy, SpaceVoid)
)

val PurpleGlowGradient = Brush.linearGradient(
    colors = listOf(CyberPurple, Color(0xFFA26BFF))
)

val CyanGlowGradient = Brush.linearGradient(
    colors = listOf(CyberCyan, Color(0xFF56E1FF))
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(WarmYellow, Color(0xFFFF9F43))
)
