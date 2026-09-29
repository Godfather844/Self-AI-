package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.Screen
import com.example.ui.theme.SoftLilacLight
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.WarmPeachPrimary

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val isCenterAction: Boolean = false
)

@Composable
fun FloatingBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(Screen.Home, "Home", Icons.Rounded.Home),
        NavItem(Screen.PlayUpload, "Play", Icons.Rounded.PlayArrow, isCenterAction = true),
        NavItem(Screen.Leaderboard, "Ranks", Icons.Rounded.Leaderboard),
        NavItem(Screen.Profile, "Profile", Icons.Rounded.Person)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating pill bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0x334338CA),
                    ambientColor = Color(0x224338CA)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavBarItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItemView(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (item.isCenterAction) {
        // Special tactile Play / Upload button
        val centerBg = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(SoftLilacPrimary, Color(0xFF6366F1))
            } else {
                listOf(Color(0xFF6366F1), SoftLilacPrimary)
            }
        )

        Box(
            modifier = Modifier
                .testTag("nav_item_${item.label.lowercase()}")
                .offset(y = (-6).dp)
                .shadow(6.dp, CircleShape, spotColor = SoftLilacPrimary)
                .clip(CircleShape)
                .background(centerBg)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    } else {
        val iconColor by animateColorAsState(
            targetValue = if (isSelected) SoftLilacPrimary else Color(0xFF94A3B8),
            label = "nav_icon_color"
        )
        val pillBgColor by animateColorAsState(
            targetValue = if (isSelected) SoftLilacLight else Color.Transparent,
            label = "nav_pill_bg"
        )

        Box(
            modifier = Modifier
                .testTag("nav_item_${item.label.lowercase()}")
                .clip(RoundedCornerShape(20.dp))
                .background(pillBgColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.label,
                    color = iconColor,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
