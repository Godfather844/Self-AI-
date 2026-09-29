package com.example.ui.screens.quiz

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuizQuestion
import com.example.ui.components.ChunkyButton
import com.example.ui.components.ChunkyButtonStyle
import com.example.ui.theme.ChunkyPrimaryBottom
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
fun VoiceAnswerView(
    question: QuizQuestion.VoiceAnswer,
    spokenText: String,
    detectedKeywords: Set<String>,
    isListening: Boolean,
    isAnswerSubmitted: Boolean,
    isCorrect: Boolean,
    onSpeechRecognized: (String) -> Unit,
    onSetListening: (Boolean) -> Unit,
    onSubmitAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Android Speech Recognizer Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onSetListening(false)
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognized = spokenMatches?.firstOrNull() ?: ""
            if (recognized.isNotBlank()) {
                onSpeechRecognized(recognized)
            }
        }
    }

    // Permission launcher for Record Audio
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onSetListening(true)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, question.questionPrompt)
            }
            try {
                speechLauncher.launch(intent)
            } catch (_: Exception) {
                onSetListening(false)
                // Fallback to sample model answer for demo if recognizer activity missing
                onSpeechRecognized(question.sampleModelAnswer)
            }
        } else {
            // Permission denied: Provide simulated response so testing is not blocked
            onSpeechRecognized(question.sampleModelAnswer)
        }
    }

    // Pulsing 3D ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Format Chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(RoseRedLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🎙️ Speech-to-Answer Voice Recall",
                    color = RoseRedPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${detectedKeywords.size}/${question.minKeywordsRequired} keywords matched",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Question Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("voice_question_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Oral Concept Drill",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = question.questionPrompt,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 23.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Pulsing 3D Microphone Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Pulse Ring
            if (isListening) {
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(SoftLilacPrimary.copy(alpha = 0.25f))
                )
            }

            // 3D Chunky Mic Orb
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                if (isListening) RoseRedPrimary else SoftLilacPrimary,
                                if (isListening) Color(0xFFBE123C) else ChunkyPrimaryBottom
                            )
                        )
                    )
                    .clickable(enabled = !isAnswerSubmitted) {
                        recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    }
                    .testTag("pulse_mic_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Rounded.Mic else Icons.Rounded.MicNone,
                    contentDescription = "Speak Now",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Text(
            text = if (isListening) "Listening... Speak your concept now 🎙️" else "Tap Mic to Speak Answer 🗣️",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isListening) RoseRedPrimary else SoftLilacPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Spoken Transcript Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = "Speech Transcript:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = spokenText.ifBlank { "No speech recorded yet. Tap the microphone or use the simulation chip below." },
                    fontSize = 13.5.sp,
                    color = if (spokenText.isBlank()) TextMuted else TextPrimary,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Required Keywords Pills (highlight in green when detected)
        Text(
            text = "Required Concept Keywords (Need at least ${question.minKeywordsRequired}):",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            question.requiredKeywords.forEach { keyword ->
                val isMatched = detectedKeywords.contains(keyword)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isMatched) MintGreenLight else Color(0xFFF1F5F9))
                        .border(
                            width = 1.dp,
                            color = if (isMatched) MintGreenPrimary else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isMatched) {
                            Icon(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MintGreenPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = keyword,
                            fontSize = 12.sp,
                            fontWeight = if (isMatched) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isMatched) MintGreenPrimary else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simulation shortcut for emulators / noisy testing
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SkyBlueLight)
                .clickable(enabled = !isAnswerSubmitted) {
                    onSpeechRecognized(question.sampleModelAnswer)
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⚡ Tap to populate model answer for testing",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = SkyBluePrimary
            )
        }

        if (!isAnswerSubmitted) {
            Spacer(modifier = Modifier.height(20.dp))
            val isReady = detectedKeywords.size >= question.minKeywordsRequired || spokenText.length >= 10
            ChunkyButton(
                text = if (isReady) "Evaluate Oral Response ✨" else "Speak Answer to Continue",
                onClick = onSubmitAnswer,
                style = ChunkyButtonStyle.PRIMARY,
                enabled = isReady,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_voice_button"
            )
        }
    }
}
