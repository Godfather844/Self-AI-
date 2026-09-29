package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChunkyGreenBottom
import com.example.ui.theme.ChunkyPeachBottom
import com.example.ui.theme.ChunkyPrimaryBottom
import com.example.ui.theme.ChunkyRedBottom
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.RoseRedPrimary
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.WarmPeachPrimary

enum class ChunkyButtonStyle {
    PRIMARY,
    PEACH,
    GREEN,
    RED,
    OUTLINE
}

@Composable
fun ChunkyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ChunkyButtonStyle = ChunkyButtonStyle.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "chunky_button",
    cornerRadius: Dp = 20.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (topColor, bottomColor, textColor) = when (style) {
        ChunkyButtonStyle.PRIMARY -> Triple(SoftLilacPrimary, ChunkyPrimaryBottom, Color.White)
        ChunkyButtonStyle.PEACH -> Triple(WarmPeachPrimary, ChunkyPeachBottom, Color.White)
        ChunkyButtonStyle.GREEN -> Triple(MintGreenPrimary, ChunkyGreenBottom, Color.White)
        ChunkyButtonStyle.RED -> Triple(RoseRedPrimary, ChunkyRedBottom, Color.White)
        ChunkyButtonStyle.OUTLINE -> Triple(Color(0xFFF1F5F9), Color(0xFFCBD5E1), Color(0xFF334155))
    }

    val shadowDepth = 4.dp
    val animatedOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowDepth else 0.dp,
        label = "button_press_offset"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Bottom bevel shadow layer (gives 3D chunky tactile depth)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = if (enabled) bottomColor else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        // Top interactive layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = animatedOffset)
                .background(
                    color = if (enabled) topColor else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(horizontal = 24.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) textColor else Color(0xFF94A3B8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}
