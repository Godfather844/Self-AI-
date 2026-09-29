package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizQuestion
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.RoseRedLight
import com.example.ui.theme.RoseRedPrimary
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SoftLilacLight
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun ConceptMatchView(
    question: QuizQuestion.ConceptMatch,
    matchedPairIds: Set<String>,
    selectedTermId: String?,
    selectedDefId: String?,
    isPairMismatch: Boolean,
    isAnswerSubmitted: Boolean,
    onSelectTerm: (String) -> Unit,
    onSelectDef: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Shake animation for mismatch
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isPairMismatch) {
        if (isPairMismatch) {
            shakeOffset.animateTo(12f, tween(50))
            shakeOffset.animateTo(-12f, tween(50))
            shakeOffset.animateTo(8f, tween(50))
            shakeOffset.animateTo(-8f, tween(50))
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    // Scramble definitions predictably by ID hash
    val scrambledDefs = remember(question.pairs) {
        question.pairs.sortedBy { it.id.hashCode() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Tag chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SkyBlueLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🧩 Concept Pair Matching",
                    color = SkyBluePrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${matchedPairIds.size}/${question.pairs.size} pairs matched",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = question.prompt,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        // Two Column Grid: Left Terms vs Right Definitions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Column: Terms
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Concepts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                question.pairs.forEach { pair ->
                    val isMatched = matchedPairIds.contains(pair.id)
                    val isSelected = selectedTermId == pair.id
                    val isMismatch = isSelected && isPairMismatch

                    MatchCard(
                        text = pair.term,
                        isMatched = isMatched,
                        isSelected = isSelected,
                        isMismatch = isMismatch,
                        onClick = { onSelectTerm(pair.id) }
                    )
                }
            }

            // Right Column: Definitions
            Column(
                modifier = Modifier.weight(1.3f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Definitions / Criteria",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                scrambledDefs.forEach { pair ->
                    val isMatched = matchedPairIds.contains(pair.id)
                    val isSelected = selectedDefId == pair.id
                    val isMismatch = isSelected && isPairMismatch

                    MatchCard(
                        text = pair.definition,
                        isMatched = isMatched,
                        isSelected = isSelected,
                        isMismatch = isMismatch,
                        onClick = { onSelectDef(pair.id) }
                    )
                }
            }
        }

        if (!isAnswerSubmitted) {
            Spacer(modifier = Modifier.height(20.dp))
            val allMatched = matchedPairIds.size >= question.pairs.size
            ChunkyButton(
                text = if (allMatched) "Continue With All Matches ✨" else "Match All Pairs (${matchedPairIds.size}/${question.pairs.size})",
                onClick = onSubmitAnswer,
                style = if (allMatched) ChunkyButtonStyle.GREEN else ChunkyButtonStyle.PRIMARY,
                enabled = allMatched,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_concept_match"
            )
        }
    }
}

@Composable
private fun MatchCard(
    text: String,
    isMatched: Boolean,
    isSelected: Boolean,
    isMismatch: Boolean,
    onClick: () -> Unit
) {
    val (bg, border, textCol) = when {
        isMatched -> Triple(MintGreenLight, MintGreenPrimary, MintGreenPrimary)
        isMismatch -> Triple(RoseRedLight, RoseRedPrimary, RoseRedPrimary)
        isSelected -> Triple(SoftLilacLight, SoftLilacPrimary, SoftLilacPrimary)
        else -> Triple(Color.White, Color(0xFFE2E8F0), TextPrimary)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else if (isMatched) 1.dp else 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected || isMatched || isMismatch) 2.dp else 1.dp,
                color = border,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isMatched) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected || isMatched) FontWeight.Bold else FontWeight.Medium,
                    color = textCol,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )

                if (isMatched) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(MintGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Matched",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
