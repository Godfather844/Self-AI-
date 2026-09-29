package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.WarmPeachLight
import com.example.ui.theme.WarmPeachPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInTheBlanksView(
    question: QuizQuestion.FillInTheBlanks,
    filledBlanks: List<String>,
    availablePool: List<String>,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean,
    onTapPoolChip: (String) -> Unit,
    onRemoveFilledBlank: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Format Header Chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmPeachLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🧩 Tap-to-Fill Blank",
                    color = WarmPeachPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${filledBlanks.size}/${question.blankPlaceholders.size} filled",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Prompt Instruction
        Text(
            text = question.prompt,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Sentence Card with Interactive Blank Slots
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fill_blanks_sentence_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = question.sentencePrefix,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    // Blank slot 0
                    BlankSlot(
                        index = 0,
                        filledWord = filledBlanks.getOrNull(0),
                        isSubmitted = isAnswerSubmitted,
                        isCorrect = isCorrect,
                        onClick = { onRemoveFilledBlank(0) }
                    )

                    if (question.sentenceMiddle.isNotEmpty()) {
                        Text(
                            text = question.sentenceMiddle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    // Blank slot 1 if exists
                    if (question.blankPlaceholders.size > 1) {
                        BlankSlot(
                            index = 1,
                            filledWord = filledBlanks.getOrNull(1),
                            isSubmitted = isAnswerSubmitted,
                            isCorrect = isCorrect,
                            onClick = { onRemoveFilledBlank(1) }
                        )
                    }

                    if (question.sentenceSuffix.isNotEmpty()) {
                        Text(
                            text = question.sentenceSuffix,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Word Bank Title
        Text(
            text = "Word Bank (Tap to fill):",
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Scrambled Pool of Rounded Chips
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (availablePool.isEmpty()) {
                    Text(
                        text = "All words assigned! Tap a filled slot above to return chips.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    availablePool.forEach { chip ->
                        WordBankChip(
                            text = chip,
                            enabled = !isAnswerSubmitted,
                            onClick = { onTapPoolChip(chip) }
                        )
                    }
                }
            }
        }

        if (!isAnswerSubmitted) {
            Spacer(modifier = Modifier.height(24.dp))
            val isReady = filledBlanks.size >= question.blankPlaceholders.size
            ChunkyButton(
                text = if (isReady) "Check Sentence ✨" else "Fill All Blanks (${filledBlanks.size}/${question.blankPlaceholders.size})",
                onClick = onSubmitAnswer,
                style = ChunkyButtonStyle.PRIMARY,
                enabled = isReady,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_blank_button"
            )
        }
    }
}

@Composable
private fun BlankSlot(
    index: Int,
    filledWord: String?,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    if (filledWord != null) {
        val (bg, border, textCol) = when {
            isSubmitted && isCorrect -> Triple(MintGreenLight, MintGreenPrimary, MintGreenPrimary)
            isSubmitted && !isCorrect -> Triple(RoseRedLight, RoseRedPrimary, RoseRedPrimary)
            else -> Triple(SoftLilacLight, SoftLilacPrimary, SoftLilacPrimary)
        }

        Box(
            modifier = Modifier
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(bg)
                .border(1.5.dp, border, RoundedCornerShape(12.dp))
                .clickable(enabled = !isSubmitted) { onClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("filled_blank_slot_$index")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = filledWord,
                    color = textCol,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (!isSubmitted) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Remove",
                        tint = textCol.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    } else {
        // Empty slot placeholder
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE2E8F0))
                .border(
                    width = 1.5.dp,
                    color = Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .testTag("empty_blank_slot_$index")
        ) {
            Text(
                text = "___",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WordBankChip(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag("word_bank_chip_$text")
    ) {
        Text(
            text = text,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            color = SoftLilacPrimary
        )
    }
}
