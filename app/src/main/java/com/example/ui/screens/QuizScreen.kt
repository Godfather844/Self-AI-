package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import android.app.Activity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ads.StartIoAdManager
import com.example.model.QuizQuestion
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.components.HeartsCounter
import com.example.ui.components.StartIoRewardVideoDialog
import com.example.ui.components.Trophy3DGraphic
import com.example.ui.screens.quiz.ConceptMatchView
import com.example.ui.screens.quiz.FillInTheBlanksView
import com.example.ui.screens.quiz.MultipleChoiceView
import com.example.ui.screens.quiz.VoiceAnswerView
import com.example.ui.theme.BackgroundSoft
import com.example.ui.theme.MintGreenLight
import com.example.ui.theme.MintGreenPrimary
import com.example.ui.theme.RoseRedLight
import com.example.ui.theme.RoseRedPrimary
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SoftLilacLight
import com.example.ui.theme.SoftLilacPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.QuizUiState

@Composable
fun QuizScreen(
    quizState: QuizUiState,
    onSelectOption: (Int) -> Unit,
    onTapPoolChip: (String) -> Unit,
    onRemoveFilledBlank: (Int) -> Unit,
    onSelectMatchTerm: (String) -> Unit,
    onSelectMatchDef: (String) -> Unit,
    onSpeechRecognized: (String) -> Unit,
    onSetVoiceListening: (Boolean) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    onExitQuiz: () -> Unit,
    onRestoreHeartWithAd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSimulatedAdPlayer by remember { mutableStateOf(false) }
    var isAdLoading by remember { mutableStateOf(false) }

    fun launchStartIoAd() {
        val activity = context as? Activity
        if (activity != null) {
            isAdLoading = true
            StartIoAdManager.showRewardedVideo(
                activity = activity,
                onRewardEarned = {
                    isAdLoading = false
                    onRestoreHeartWithAd()
                },
                onAdDismissed = {
                    isAdLoading = false
                },
                onAdFailed = {
                    // Fallback to in-app interactive video ad player
                    isAdLoading = false
                    showSimulatedAdPlayer = true
                }
            )
        } else {
            showSimulatedAdPlayer = true
        }
    }

    val currentQuestion = quizState.questions.getOrNull(quizState.currentQuestionIndex)
    val totalCount = quizState.questions.size.coerceAtLeast(1)
    val progress = (quizState.currentQuestionIndex + 1).toFloat() / totalCount

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSoft)
            .statusBarsPadding()
    ) {
        if (showSimulatedAdPlayer) {
            StartIoRewardVideoDialog(
                onRewardEarned = {
                    onRestoreHeartWithAd()
                },
                onDismiss = {
                    showSimulatedAdPlayer = false
                }
            )
        }

        if (quizState.isCompleted) {
            // Victory Celebration Screen
            QuizCompletedView(
                quizState = quizState,
                onRestart = onRestartQuiz,
                onDone = onExitQuiz
            )
        } else if (quizState.isFailed) {
            // Out of Hearts Game Over Screen with Start.io Ad integration
            QuizFailedView(
                onWatchAd = { launchStartIoAd() },
                isAdLoading = isAdLoading,
                onRetry = onRestartQuiz,
                onExit = onExitQuiz
            )
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = if (quizState.isAnswerSubmitted) 230.dp else 90.dp)
            ) {
                // Top Quiz Bar: Exit, Progress bar, and 3 Hearts counter ❤️❤️❤️
                TopQuizBar(
                    progress = progress,
                    hearts = quizState.heartsRemaining,
                    onExit = onExitQuiz,
                    onWatchAd = { launchStartIoAd() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Topic Badge
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftLilacLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "📚 ${currentQuestion.topic}",
                        color = SoftLilacPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Multi-Format Interaction View
                when (currentQuestion) {
                    is QuizQuestion.FillInTheBlanks -> {
                        FillInTheBlanksView(
                            question = currentQuestion,
                            filledBlanks = quizState.filledBlanks,
                            availablePool = quizState.availablePool,
                            isAnswerSubmitted = quizState.isAnswerSubmitted,
                            isCorrect = quizState.isCorrect,
                            onTapPoolChip = onTapPoolChip,
                            onRemoveFilledBlank = onRemoveFilledBlank,
                            onSubmitAnswer = onSubmitAnswer
                        )
                    }

                    is QuizQuestion.ConceptMatch -> {
                        ConceptMatchView(
                            question = currentQuestion,
                            matchedPairIds = quizState.matchedPairIds,
                            selectedTermId = quizState.selectedTermId,
                            selectedDefId = quizState.selectedDefId,
                            isPairMismatch = quizState.isPairMismatch,
                            isAnswerSubmitted = quizState.isAnswerSubmitted,
                            onSelectTerm = onSelectMatchTerm,
                            onSelectDef = onSelectMatchDef,
                            onSubmitAnswer = onSubmitAnswer
                        )
                    }

                    is QuizQuestion.VoiceAnswer -> {
                        VoiceAnswerView(
                            question = currentQuestion,
                            spokenText = quizState.spokenText,
                            detectedKeywords = quizState.detectedKeywords,
                            isListening = quizState.isListening,
                            isAnswerSubmitted = quizState.isAnswerSubmitted,
                            isCorrect = quizState.isCorrect,
                            onSpeechRecognized = onSpeechRecognized,
                            onSetListening = onSetVoiceListening,
                            onSubmitAnswer = onSubmitAnswer
                        )
                    }

                    is QuizQuestion.MultipleChoice -> {
                        MultipleChoiceView(
                            question = currentQuestion,
                            selectedOptionIndex = quizState.selectedOptionIndex,
                            isAnswerSubmitted = quizState.isAnswerSubmitted,
                            isCorrect = quizState.isCorrect,
                            onSelectOption = onSelectOption,
                            onSubmitAnswer = onSubmitAnswer
                        )
                    }
                }
            }

            // Slide-up Green/Red Result Sheet showing instant feedback, explanation, and XP points
            AnimatedVisibility(
                visible = quizState.isAnswerSubmitted,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                ResultBottomFeedbackSheet(
                    isCorrect = quizState.isCorrect,
                    explanation = currentQuestion.explanation,
                    xpEarned = if (quizState.isCorrect) currentQuestion.xpReward else 0,
                    onNext = onNextQuestion
                )
            }
        }
    }
}

@Composable
private fun TopQuizBar(
    progress: Float,
    hearts: Int,
    onExit: () -> Unit,
    onWatchAd: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onExit,
            modifier = Modifier.testTag("exit_quiz_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Exit Quiz",
                tint = TextSecondary
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Progress Bar smoothly updating with each completed micro-challenge
        Column(modifier = Modifier.weight(1f)) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = SoftLilacPrimary,
                trackColor = Color(0xFFE2E8F0),
                strokeCap = StrokeCap.Round
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 3 Hearts Counter ❤️❤️❤️
        HeartsCounter(heartsRemaining = hearts)

        if (hearts < 3) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEFF6FF))
                    .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(50))
                    .clickable { onWatchAd() }
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+❤️", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Ad", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                }
            }
        }
    }
}

@Composable
private fun ResultBottomFeedbackSheet(
    isCorrect: Boolean,
    explanation: String,
    xpEarned: Int,
    onNext: () -> Unit
) {
    val sheetBg = if (isCorrect) MintGreenLight else RoseRedLight
    val primaryColor = if (isCorrect) MintGreenPrimary else RoseRedPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(18.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(sheetBg)
            .border(
                width = 1.5.dp,
                color = primaryColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .padding(20.dp)
            .testTag("result_feedback_sheet")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Rounded.Check else Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (isCorrect) "Brilliant! That's Correct! 🎉" else "Almost! Here's the key: 💡",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                }

                if (isCorrect && xpEarned > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+$xpEarned XP 💎",
                            color = SkyBluePrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = explanation,
                fontSize = 13.5.sp,
                lineHeight = 19.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            ChunkyButton(
                text = "Continue ➔",
                onClick = onNext,
                style = if (isCorrect) ChunkyButtonStyle.GREEN else ChunkyButtonStyle.RED,
                modifier = Modifier.fillMaxWidth(),
                testTag = "continue_next_question_button"
            )
        }
    }
}

@Composable
private fun QuizCompletedView(
    quizState: QuizUiState,
    onRestart: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Trophy3DGraphic(size = 110.dp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Quest Mastered! 🌟",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SoftLilacPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "You cleared multi-format concept challenges with ${quizState.correctCount}/${quizState.questions.size} accuracy!",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // XP Reward Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SkyBlueLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "💎", fontSize = 24.sp)
                    Text(
                        text = "+${quizState.totalXpEarned} XP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyBluePrimary
                    )
                    Text(text = "Earned", fontSize = 11.sp, color = TextSecondary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔥", fontSize = 24.sp)
                    Text(
                        text = "+1 Day",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFEA580C)
                    )
                    Text(text = "Streak Boost", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        ChunkyButton(
            text = "Claim Rewards & Finish 🏆",
            onClick = onDone,
            style = ChunkyButtonStyle.PRIMARY,
            modifier = Modifier.fillMaxWidth(),
            testTag = "quiz_done_button"
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChunkyButton(
            text = "Replay Quest 🔄",
            onClick = onRestart,
            style = ChunkyButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun QuizFailedView(
    onWatchAd: () -> Unit,
    isAdLoading: Boolean,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "💔", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Out of Hearts!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RoseRedPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "You ran out of lives on this quest challenge.",
            fontSize = 13.5.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Start.io Rewarded Video Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF2563EB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2563EB))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Start.io Rewarded",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Life Restore 🎁",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Watch a Start.io rewarded ad to earn +1 Heart ❤️ and resume this quest immediately without losing your progress!",
                    fontSize = 12.5.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                ChunkyButton(
                    text = if (isAdLoading) "Loading Start.io Ad..." else "🎬 Watch Start.io Ad (+1 ❤️)",
                    onClick = onWatchAd,
                    enabled = !isAdLoading,
                    style = ChunkyButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
            Text(
                text = " OR ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChunkyButton(
            text = "Restart Quest from Beginning 🔄",
            onClick = onRetry,
            style = ChunkyButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        ChunkyButton(
            text = "Back to Notes 📝",
            onClick = onExit,
            style = ChunkyButtonStyle.OUTLINE,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
