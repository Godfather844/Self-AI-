package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardEntry
import com.example.model.LeaderboardFilter
import com.example.ui.components.Avatar3DBadge
import com.example.ui.components.CategoryChip
import com.example.ui.components.XpPill
import com.example.ui.theme.BackgroundSoft
import com.example.ui.theme.BronzePodiumGradient
import com.example.ui.theme.GoldenPodiumGradient
import com.example.ui.theme.SilverPodiumGradient
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
fun LeaderboardScreen(
    currentFilter: LeaderboardFilter,
    weeklyList: List<LeaderboardEntry>,
    collegeList: List<LeaderboardEntry>,
    onFilterChange: (LeaderboardFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeList = if (currentFilter == LeaderboardFilter.WEEKLY) weeklyList else collegeList
    val top1 = activeList.getOrNull(0)
    val top2 = activeList.getOrNull(1)
    val top3 = activeList.getOrNull(2)
    val remainingRanks = if (activeList.size > 3) activeList.subList(3, activeList.size) else emptyList()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp)
        ) {
            // Header Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Leaderboard",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "🏆", fontSize = 24.sp)
                }
                Text(
                    text = "Compete with peers and climb the knowledge ranks",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            // Filter Tabs: "Weekly" vs "Batchmates / College"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Weekly Tab
                    val isWeekly = currentFilter == LeaderboardFilter.WEEKLY
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isWeekly) SoftLilacPrimary else Color.Transparent)
                            .clickable { onFilterChange(LeaderboardFilter.WEEKLY) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Weekly 🌐",
                            fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.Medium,
                            color = if (isWeekly) Color.White else TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // College Tab
                    val isCollege = currentFilter == LeaderboardFilter.COLLEGE_BATCHMATES
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isCollege) SoftLilacPrimary else Color.Transparent)
                            .clickable { onFilterChange(LeaderboardFilter.COLLEGE_BATCHMATES) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Batchmates / College 🎓",
                            fontWeight = if (isCollege) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCollege) Color.White else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (activeList.isEmpty()) {
                Spacer(modifier = Modifier.height(48.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🏆", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Users in Database Yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Database me abhi koi user ya study data nahi hai. Sign in karke pehla quest complete karein aur #1 rank par aayein!",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // 3D Podium for Top 3 ranks
                Podium3DSection(
                    top1 = top1,
                    top2 = top2,
                    top3 = top3
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (remainingRanks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚡ Live Real-Time Leaderboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "You are currently holding the #1 position on the real-time leaderboard! Classmates will appear here as they register and earn XP.",
                                fontSize = 12.5.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Scrollable list for other ranks (#4, #5, #6...)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(remainingRanks) { entry ->
                            RankListItem(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Podium3DSection(
    top1: LeaderboardEntry?,
    top2: LeaderboardEntry?,
    top3: LeaderboardEntry?
) {
    if (top1 == null) return

    if (top2 == null && top3 == null) {
        // Single user on real leaderboard: Center the Gold Champion podium
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.widthIn(max = 220.dp)) {
                PodiumColumn(
                    entry = top1,
                    rank = 1,
                    podiumHeight = 140.dp,
                    podiumBrush = GoldenPodiumGradient,
                    medalEmoji = "🥇",
                    badgeColor = Color(0xFFB45309),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver Podium Block)
        if (top2 != null) {
            PodiumColumn(
                entry = top2,
                rank = 2,
                podiumHeight = 100.dp,
                podiumBrush = SilverPodiumGradient,
                medalEmoji = "🥈",
                badgeColor = Color(0xFF64748B),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        // 1st Place (Gold Podium Block - Tallest)
        PodiumColumn(
            entry = top1,
            rank = 1,
            podiumHeight = 135.dp,
            podiumBrush = GoldenPodiumGradient,
            medalEmoji = "🥇",
            badgeColor = Color(0xFFB45309),
            modifier = Modifier.weight(1.1f)
        )

        // 3rd Place (Bronze Podium Block)
        if (top3 != null) {
            Spacer(modifier = Modifier.width(8.dp))
            PodiumColumn(
                entry = top3,
                rank = 3,
                podiumHeight = 80.dp,
                podiumBrush = BronzePodiumGradient,
                medalEmoji = "🥉",
                badgeColor = Color(0xFF9A3412),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    rank: Int,
    podiumHeight: androidx.compose.ui.unit.Dp,
    podiumBrush: Brush,
    medalEmoji: String,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Avatar + Medal
        Box(contentAlignment = Alignment.TopEnd) {
            Avatar3DBadge(
                emoji = entry.avatarEmoji,
                size = if (rank == 1) 56.dp else 48.dp,
                borderColor = badgeColor
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(2.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = medalEmoji, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.name,
            fontSize = if (rank == 1) 13.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = "${entry.xp} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SoftLilacPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3D Colored Podium Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .shadow(6.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(podiumBrush),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    color = Color.White,
                    fontSize = if (rank == 1) 28.sp else 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = entry.badgeLabel,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RankListItem(entry: LeaderboardEntry) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentUser) SoftLilacLight else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (entry.isCurrentUser) 1.5.dp else 1.dp,
                color = if (entry.isCurrentUser) SoftLilacPrimary else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("rank_item_${entry.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Text(
                text = "#${entry.rank}",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (entry.isCurrentUser) SoftLilacPrimary else TextSecondary,
                modifier = Modifier.width(32.dp)
            )

            // Avatar
            Avatar3DBadge(
                emoji = entry.avatarEmoji,
                size = 40.dp,
                borderColor = if (entry.isCurrentUser) SoftLilacPrimary else Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (entry.isCurrentUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        CategoryChip(
                            text = "YOU",
                            backgroundColor = SoftLilacPrimary,
                            textColor = Color.White
                        )
                    }
                }
                Text(
                    text = entry.college,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            // Score Pill
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "💎 ${entry.xp} XP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SkyBluePrimary
                )
                Text(
                    text = "🔥 ${entry.streak}d streak",
                    fontSize = 11.sp,
                    color = WarmPeachPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
