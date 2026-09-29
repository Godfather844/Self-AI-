package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Pastels & Vibrants
val SkyBlueLight = Color(0xFFE0F2FE)
val SkyBlueMid = Color(0xFF7DD3FC)
val SkyBluePrimary = Color(0xFF0284C7)
val SkyBlueDark = Color(0xFF0369A1)

val SoftLilacLight = Color(0xFFF3E8FF)
val SoftLilacMid = Color(0xFFC084FC)
val SoftLilacPrimary = Color(0xFF8B5CF6)
val SoftLilacDark = Color(0xFF6D28D9)

val WarmPeachLight = Color(0xFFFFEDD5)
val WarmPeachMid = Color(0xFFFDBA74)
val WarmPeachPrimary = Color(0xFFFB923C)
val WarmPeachDark = Color(0xFFC2410C)

val MintGreenLight = Color(0xFFD1FAE5)
val MintGreenPrimary = Color(0xFF10B981)
val MintGreenDark = Color(0xFF047857)

val RoseRedLight = Color(0xFFFFE4E6)
val RoseRedPrimary = Color(0xFFF43F5E)
val RoseRedDark = Color(0xFFBE123C)

val AmberGoldLight = Color(0xFFFEF3C7)
val AmberGoldPrimary = Color(0xFFF59E0B)
val AmberGoldDark = Color(0xFFB45309)

// Surface & Text
val BackgroundSoft = Color(0xFFF8FAFC)
val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceCardBorder = Color(0xFFE2E8F0)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF475569)
val TextMuted = Color(0xFF94A3B8)

// Chunky Button 3D Shadow Layers
val ChunkyPrimaryBottom = Color(0xFF4338CA)
val ChunkyPeachBottom = Color(0xFFC2410C)
val ChunkyGreenBottom = Color(0xFF065F46)
val ChunkyRedBottom = Color(0xFF9F1239)

// Gradients
val SkyPastelGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFFDDD6FE))
)

val HeroChampionGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF60A5FA), Color(0xFF818CF8), Color(0xFFA78BFA))
)

val PeachLilacGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFEDD5), Color(0xFFFCE7F3), Color(0xFFEDE9FE))
)

val GoldenPodiumGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFCD34D), Color(0xFFF59E0B))
)

val SilverPodiumGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
)

val BronzePodiumGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFFDBA74), Color(0xFFB45309))
)

val CardGlowBorder = Brush.linearGradient(
    colors = listOf(Color(0xFFE0E7FF), Color(0xFFFCE7F3))
)
