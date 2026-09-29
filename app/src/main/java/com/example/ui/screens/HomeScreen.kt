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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RevisionQuest
import com.example.model.UserProfile
import com.example.navigation.Screen
import com.example.ui.components.Avatar3DBadge
import com.example.ui.components.CategoryChip
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.components.HeartsCounter
import com.example.ui.components.StreakPill
import com.example.ui.components.Trophy3DGraphic
import com.example.ui.components.XpPill
import com.example.ui.theme.BackgroundSoft
import com.example.ui.theme.HeroChampionGradient
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.MintGreenPrimary
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
fun HomeScreen(
    userProfile: UserProfile,
    quests: List<RevisionQuest>,
    onStartQuest: (RevisionQuest) -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                .padding(bottom = 96.dp) // Space for floating bottom nav
        ) {
            // Top Header: User avatar with name, Student chip, and XP counter pill
            HeaderSection(
                userProfile = userProfile,
                onAvatarClick = onNavigateToProfile
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Streak Status & Daily Motivation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StreakPill(streakDays = userProfile.currentStreak)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MintGreenLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Active",
                            tint = MintGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Daily Goal 80%",
                            color = MintGreenPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Banner: Pastel blue gradient card "Rise Up Quiz Champion"
            HeroBannerSection(
                onPlayNowClick = onNavigateToUpload
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Recent Quests & Revision Topics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Quests & Revision",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Synthesized from your college notes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "See All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftLilacPrimary,
                    modifier = Modifier.clickable { onNavigateToUpload() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Quest Cards
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(quests) { quest ->
                    QuestCardItem(
                        quest = quest,
                        onClick = { onStartQuest(quest) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Note Snap Callout
            QuickSnapCard(
                onSnapClick = onNavigateToUpload
            )
        }
    }
}

@Composable
private fun HeaderSection(
    userProfile: UserProfile,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onAvatarClick() }
        ) {
            Avatar3DBadge(
                emoji = userProfile.avatarEmoji,
                size = 52.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    CategoryChip(
                        text = "Student",
                        backgroundColor = SoftLilacLight,
                        textColor = SoftLilacPrimary
                    )
                }

                Text(
                    text = userProfile.college,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }

        // Hearts and XP Counter Pills
        Row(verticalAlignment = Alignment.CenterVertically) {
            HeartsCounter(heartsRemaining = userProfile.heartsRemaining)
            Spacer(modifier = Modifier.width(8.dp))
            XpPill(xp = userProfile.xp)
        }
    }
}

@Composable
private fun HeroBannerSection(
    onPlayNowClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("hero_banner_champion")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroChampionGradient)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⚡ DAILY CHALLENGE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Rise Up Quiz Champion",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Brew notes into high-yield MCQs & claim +50 XP today!",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ChunkyButton(
                        text = "Play Quest 🎯",
                        onClick = onPlayNowClick,
                        style = ChunkyButtonStyle.PEACH,
                        cornerRadius = 14.dp,
                        modifier = Modifier.fillMaxWidth(0.85f),
                        testTag = "hero_play_button"
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Trophy3DGraphic(size = 90.dp)
            }
        }
    }
}

@Composable
private fun QuestCardItem(
    quest: RevisionQuest,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(220.dp)
            .testTag("quest_card_${quest.id}")
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(quest.accentColorHex).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = quest.iconEmoji, fontSize = 22.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (quest.difficulty) {
                                "Hard" -> Color(0xFFFFE4E6)
                                "Medium" -> Color(0xFFFEF3C7)
                                else -> Color(0xFFD1FAE5)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = quest.difficulty,
                        color = when (quest.difficulty) {
                            "Hard" -> Color(0xFFBE123C)
                            "Medium" -> Color(0xFFB45309)
                            else -> Color(0xFF047857)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = quest.topicName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = quest.subject,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quest.solvedQuestions}/${quest.totalQuestions} Solved",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${(quest.progressPercent * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = Color(quest.accentColorHex),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { quest.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(quest.accentColorHex),
                trackColor = Color(0xFFE2E8F0),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun QuickSnapCard(
    onSnapClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmPeachLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onSnapClick() }
            .testTag("quick_snap_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(WarmPeachPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📸", fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Snap Today's Class Notes",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Take a photo to generate new quests",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Go",
                tint = WarmPeachPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
