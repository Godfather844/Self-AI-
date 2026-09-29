package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.WorkspacePremium
import android.app.Activity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ads.StartIoAdManager
import com.example.model.BadgeItem
import com.example.model.UserProfile
import com.example.ui.components.Avatar3DBadge
import com.example.ui.components.CategoryChip
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.components.StartIoRewardVideoDialog
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.AmberGoldPrimary
import com.example.ui.theme.BackgroundSoft
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.PeachLilacGradient
import com.example.ui.theme.RoseRedLight
import com.example.ui.theme.RoseRedPrimary
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SoftLilacLight
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmPeachLight
import com.example.ui.theme.WarmPeachPrimary

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    badges: List<BadgeItem>,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAdConfigDialog by remember { mutableStateOf(false) }
    var currentAppId by remember { mutableStateOf(StartIoAdManager.appId) }
    var isTestMode by remember { mutableStateOf(StartIoAdManager.isTestAdsEnabled) }

    if (showAdConfigDialog) {
        var inputAppId by remember { mutableStateOf(currentAppId) }
        var inputTestMode by remember { mutableStateOf(isTestMode) }

        Dialog(onDismissRequest = { showAdConfigDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Configure Start.io Ads 📢",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter your custom Start.io App ID from portal.start.io",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputAppId,
                        onValueChange = { inputAppId = it },
                        label = { Text("Start.io App ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftLilacPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Test Ads Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Disable when building final release for Play Store", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = inputTestMode,
                            onCheckedChange = { inputTestMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2563EB))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ChunkyButton(
                        text = "Save & Apply Changes ✓",
                        onClick = {
                            if (inputAppId.isNotBlank()) {
                                currentAppId = inputAppId.trim()
                                isTestMode = inputTestMode
                                StartIoAdManager.updateConfiguration(
                                    context = context,
                                    newAppId = currentAppId,
                                    testMode = isTestMode
                                )
                            }
                            showAdConfigDialog = false
                        },
                        style = ChunkyButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ChunkyButton(
                        text = "Cancel",
                        onClick = { showAdConfigDialog = false },
                        style = ChunkyButtonStyle.OUTLINE,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Profile Header Card with 3D Avatar, Level Title & College Details
            UserProfileCard(userProfile = userProfile)

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Grid (2x2): Total Quests, Longest Streak, Accuracy %, Total XP
            Text(
                text = "Learning Statistics",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatsGridSection(userProfile = userProfile)

            Spacer(modifier = Modifier.height(24.dp))

            // Badges & Achievements Shelf
            Text(
                text = "Badges & Achievements",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            BadgesShelfSection(badges = badges)

            Spacer(modifier = Modifier.height(20.dp))

            // Database & Account Sync Status Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🗄️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Database & Cloud Sync",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Local Room SQLite DB:", fontSize = 12.5.sp, color = TextSecondary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("✓ Saved & Active", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Firebase Auth & Firestore:", fontSize = 12.5.sp, color = TextSecondary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE0E7FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("✓ Synced to Cloud", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SoftLilacPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Registered Account:", fontSize = 12.5.sp, color = TextSecondary)
                        Text(userProfile.email, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start.io Ads Integration Status & Custom App ID Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📢", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start.io Ads Integration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTestMode) Color(0xFFFEF3C7) else Color(0xFFDCFCE7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isTestMode) "Test Mode" else "Live Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTestMode) Color(0xFFB45309) else Color(0xFF15803D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active App ID:", fontSize = 12.5.sp, color = TextSecondary)
                        Text(
                            text = currentAppId,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Ad Unit:", fontSize = 12.5.sp, color = TextSecondary)
                        Text(
                            text = "Rewarded Video (+1 ❤️ Heart)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ChunkyButton(
                        text = "⚙️ Configure My Start.io App ID",
                        onClick = { showAdConfigDialog = true },
                        style = ChunkyButtonStyle.OUTLINE,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout / Switch Account Action
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ChunkyButton(
                    text = "Log Out / Switch Account",
                    onClick = onLogout,
                    style = ChunkyButtonStyle.OUTLINE,
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "profile_logout_button"
                )
            }
        }
    }
}

@Composable
private fun UserProfileCard(userProfile: UserProfile) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("user_profile_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PeachLilacGradient)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3D Avatar
                Avatar3DBadge(
                    emoji = userProfile.avatarEmoji,
                    size = 72.dp,
                    borderColor = SoftLilacPrimary,
                    bgColor = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = userProfile.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Overall Level Title Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SoftLilacPrimary)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "⭐ ${userProfile.levelTitle}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile.college,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Text(
                    text = userProfile.branch,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun StatsGridSection(userProfile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Total Quests & Longest Streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Quests Solved",
                value = "${userProfile.totalQuestsSolved}",
                icon = "🎯",
                bgColor = SkyBlueLight,
                accentColor = SkyBluePrimary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Longest Streak",
                value = "${userProfile.longestStreak} Days",
                icon = "🔥",
                bgColor = WarmPeachLight,
                accentColor = WarmPeachPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Accuracy & Total XP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Accuracy %",
                value = "${userProfile.accuracyPercentage}%",
                icon = "⚡",
                bgColor = MintGreenLight,
                accentColor = MintGreenPrimary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Total XP",
                value = "${userProfile.xp}",
                icon = "💎",
                bgColor = SoftLilacLight,
                accentColor = SoftLilacPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    bgColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun BadgesShelfSection(badges: List<BadgeItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        badges.forEach { badge ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF8FAFC)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (badge.isUnlocked) 3.dp else 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (badge.isUnlocked) Color(0xFFE2E8F0) else Color(0xFFCBD5E1),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) AmberGoldLight else Color(0xFFE2E8F0)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badge.isUnlocked) badge.iconEmoji else "🔒",
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = badge.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) TextPrimary else TextMuted
                        )
                        Text(
                            text = badge.description,
                            fontSize = 11.5.sp,
                            color = if (badge.isUnlocked) TextSecondary else TextMuted
                        )
                    }

                    if (badge.isUnlocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MintGreenLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Unlocked",
                                color = MintGreenPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
