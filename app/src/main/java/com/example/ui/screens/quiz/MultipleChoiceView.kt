package com.example.ui.screens.quiz

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun MultipleChoiceView(
    question: QuizQuestion.MultipleChoice,
    selectedOptionIndex: Int?,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Tag badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (question.isTrueFalse) WarmPeachLight else MintGreenLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (question.isTrueFalse) "⚡ Quick True/False Card" else "🎯 Concept MCQ",
                    color = if (question.isTrueFalse) WarmPeachPrimary else MintGreenPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Conceptual Question Card with soft rounded corners (24.dp)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_mcq_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (question.isTrueFalse) "Rapid-Fire Verification" else "Concept Verification",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = question.questionText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (question.isTrueFalse) {
            // Rapid-Fire Chunky True / False Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOptionIndex == index
                    val isTrueOption = index == 0
                    val (borderColor, bgColor, textColor) = when {
                        !isAnswerSubmitted && isSelected -> {
                            Triple(
                                if (isTrueOption) MintGreenPrimary else RoseRedPrimary,
                                if (isTrueOption) MintGreenLight else RoseRedLight,
                                if (isTrueOption) MintGreenPrimary else RoseRedPrimary
                            )
                        }
                        isAnswerSubmitted && index == question.correctOptionIndex -> {
                            Triple(MintGreenPrimary, MintGreenLight, MintGreenPrimary)
                        }
                        isAnswerSubmitted && isSelected && !isCorrect -> {
                            Triple(RoseRedPrimary, RoseRedLight, RoseRedPrimary)
                        }
                        else -> {
                            Triple(Color(0xFFE2E8F0), Color.White, TextPrimary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected || (isAnswerSubmitted && index == question.correctOptionIndex)) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(enabled = !isAnswerSubmitted) {
                                onSelectOption(index)
                            }
                            .testTag("tf_option_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isTrueOption) Icons.Rounded.Check else Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                fontSize = 14.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        } else {
            // Standard 4 rounded selectable MCQ options
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                question.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedOptionIndex == index
                    val optionPrefix = when (index) {
                        0 -> "A"
                        1 -> "B"
                        2 -> "C"
                        else -> "D"
                    }

                    val (borderColor, bgColor, textColor) = when {
                        !isAnswerSubmitted && isSelected -> {
                            Triple(SoftLilacPrimary, SoftLilacLight, SoftLilacPrimary)
                        }
                        isAnswerSubmitted && index == question.correctOptionIndex -> {
                            Triple(MintGreenPrimary, MintGreenLight, MintGreenPrimary)
                        }
                        isAnswerSubmitted && isSelected && !isCorrect -> {
                            Triple(RoseRedPrimary, RoseRedLight, RoseRedPrimary)
                        }
                        else -> {
                            Triple(Color(0xFFE2E8F0), Color.White, TextPrimary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected || (isAnswerSubmitted && index == question.correctOptionIndex)) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(enabled = !isAnswerSubmitted) {
                                onSelectOption(index)
                            }
                            .testTag("mcq_option_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionPrefix,
                                    color = textColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Check Answer button if not yet submitted
        if (!isAnswerSubmitted) {
            Spacer(modifier = Modifier.height(24.dp))
            ChunkyButton(
                text = "Check Answer ✨",
                onClick = onSubmitAnswer,
                style = ChunkyButtonStyle.PRIMARY,
                enabled = selectedOptionIndex != null,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_quiz_answer"
            )
        }
    }
}
