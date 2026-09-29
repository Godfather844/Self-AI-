package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBlueMid
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SoftLilacLight
import com.example.ui.theme.SoftLilacMid
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.WarmPeachLight
import com.example.ui.theme.WarmPeachMid
import com.example.ui.theme.WarmPeachPrimary

@Composable
fun Playful3DMascot(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer soft glow shadow
        drawCircle(
            color = Color(0x338B5CF6),
            radius = w * 0.44f,
            center = Offset(w * 0.5f, h * 0.54f)
        )

        // Mascot Body Sphere (Soft lilac with 3D gradient)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE9D5FF), SoftLilacMid, SoftLilacPrimary),
                center = Offset(w * 0.38f, h * 0.38f),
                radius = w * 0.45f
            ),
            radius = w * 0.36f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // Cute Visor / Glass Eye screen
        val visorWidth = w * 0.46f
        val visorHeight = h * 0.22f
        drawRoundRect(
            color = Color(0xFF1E1B4B),
            topLeft = Offset(w * 0.27f, h * 0.39f),
            size = Size(visorWidth, visorHeight),
            cornerRadius = CornerRadius(visorHeight * 0.5f, visorHeight * 0.5f)
        )

        // Cute glowing cyan robot eyes
        drawCircle(
            color = Color(0xFF38BDF8),
            radius = w * 0.045f,
            center = Offset(w * 0.40f, h * 0.50f)
        )
        drawCircle(
            color = Color(0xFF38BDF8),
            radius = w * 0.045f,
            center = Offset(w * 0.60f, h * 0.50f)
        )

        // Eye highlights
        drawCircle(
            color = Color.White,
            radius = w * 0.015f,
            center = Offset(w * 0.39f, h * 0.49f)
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.015f,
            center = Offset(w * 0.59f, h * 0.49f)
        )

        // Golden Sparkle / Antenna top
        val sparkPath = Path().apply {
            moveTo(w * 0.5f, h * 0.06f)
            lineTo(w * 0.54f, h * 0.14f)
            lineTo(w * 0.62f, h * 0.16f)
            lineTo(w * 0.55f, h * 0.20f)
            lineTo(w * 0.57f, h * 0.28f)
            lineTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.43f, h * 0.28f)
            lineTo(w * 0.45f, h * 0.20f)
            lineTo(w * 0.38f, h * 0.16f)
            lineTo(w * 0.46f, h * 0.14f)
            close()
        }
        drawPath(
            path = sparkPath,
            brush = Brush.verticalGradient(listOf(Color(0xFFFDE047), Color(0xFFF59E0B)))
        )
    }
}

@Composable
fun Trophy3DGraphic(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Drop shadow
        drawCircle(
            color = Color(0x33000000),
            radius = w * 0.32f,
            center = Offset(w * 0.5f, h * 0.88f)
        )

        // Base stand (3D tiered)
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFD97706), Color(0xFF78350F))),
            topLeft = Offset(w * 0.28f, h * 0.78f),
            size = Size(w * 0.44f, h * 0.14f),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )
        drawRoundRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFB45309))),
            topLeft = Offset(w * 0.34f, h * 0.70f),
            size = Size(w * 0.32f, h * 0.10f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // Cup stem
        drawRect(
            brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFFDE68A), Color(0xFFD97706))),
            topLeft = Offset(w * 0.44f, h * 0.56f),
            size = Size(w * 0.12f, h * 0.16f)
        )

        // Handles (left & right)
        drawCircle(
            color = Color(0xFFF59E0B),
            radius = w * 0.18f,
            center = Offset(w * 0.22f, h * 0.38f)
        )
        drawCircle(
            color = Color(0xFF60A5FA), // Cutout back color
            radius = w * 0.10f,
            center = Offset(w * 0.22f, h * 0.38f)
        )
        drawCircle(
            color = Color(0xFFF59E0B),
            radius = w * 0.18f,
            center = Offset(w * 0.78f, h * 0.38f)
        )
        drawCircle(
            color = Color(0xFF60A5FA),
            radius = w * 0.10f,
            center = Offset(w * 0.78f, h * 0.38f)
        )

        // Main Cup Body with 3D sheen
        val cupPath = Path().apply {
            moveTo(w * 0.24f, h * 0.20f)
            lineTo(w * 0.76f, h * 0.20f)
            lineTo(w * 0.68f, h * 0.54f)
            cubicTo(w * 0.62f, h * 0.62f, w * 0.38f, h * 0.62f, w * 0.32f, h * 0.54f)
            close()
        }
        drawPath(
            path = cupPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFEF08A), Color(0xFFFBBF24), Color(0xFFD97706)),
                center = Offset(w * 0.45f, h * 0.32f),
                radius = w * 0.35f
            )
        )

        // Star on cup
        drawCircle(
            color = Color.White,
            radius = w * 0.05f,
            center = Offset(w * 0.50f, h * 0.36f)
        )
    }
}

@Composable
fun Avatar3DBadge(
    emoji: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    borderColor: Color = SoftLilacPrimary,
    bgColor: Color = SoftLilacLight
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.5.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = (size.value * 0.48).sp
        )
    }
}
