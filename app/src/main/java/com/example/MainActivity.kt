package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ads.StartIoAdManager
import com.example.navigation.Screen
import com.example.ui.components.FloatingBottomNavBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.PlayUploadScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SelfAiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Start.io Ads SDK & preload rewarded video ad
        StartIoAdManager.initialize(this)
        StartIoAdManager.preloadRewardedVideo(this)

        setContent {
            MyApplicationTheme {
                SelfAiApp()
            }
        }
    }
}

@Composable
fun SelfAiApp(
    viewModel: SelfAiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen = uiState.currentScreen

    // Handle system back navigation
    BackHandler(enabled = currentScreen != Screen.Home && currentScreen != Screen.Auth) {
        viewModel.handleBack()
    }

    val showBottomBar = currentScreen in listOf(
        Screen.Home,
        Screen.PlayUpload,
        Screen.Leaderboard,
        Screen.Profile
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Screen content transition
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.Auth -> {
                        AuthScreen(
                            errorMessage = uiState.authErrorMessage,
                            isLoading = uiState.isLoadingAuth,
                            onLoginSuccess = { email, password, college ->
                                viewModel.login(email, password, college)
                            },
                            onSignUpSuccess = { name, email, password, college, branch ->
                                viewModel.signUp(name, email, password, college, branch)
                            },
                            onGoogleSignIn = { email, name ->
                                viewModel.signInWithGoogle(email, name)
                            },
                            onCheckGoogleAccount = { email, onResult ->
                                viewModel.checkGoogleAccount(email, onResult)
                            },
                            onDirectGoogleLogin = { email ->
                                viewModel.directGoogleLogin(email)
                            },
                            onCompleteGoogleRegistration = { email, name, college, branch, avatar ->
                                viewModel.completeGoogleRegistration(email, name, college, branch, avatar)
                            }
                        )
                    }

                    Screen.Home -> {
                        HomeScreen(
                            userProfile = uiState.userProfile,
                            quests = uiState.quests,
                            onStartQuest = { quest ->
                                viewModel.selectNote(
                                    topicName = quest.topicName,
                                    noteText = "Class notes for ${quest.topicName} in ${quest.subject}."
                                )
                                viewModel.brewDailyQuest()
                            },
                            onNavigateToUpload = {
                                viewModel.navigateTo(Screen.PlayUpload)
                            },
                            onNavigateToProfile = {
                                viewModel.navigateTo(Screen.Profile)
                            }
                        )
                    }

                    Screen.PlayUpload -> {
                        PlayUploadScreen(
                            selectedTopic = uiState.selectedTopicName,
                            selectedNoteText = uiState.selectedNoteText,
                            selectedUri = uiState.selectedNoteUri,
                            isBrewing = uiState.isBrewing,
                            onTopicSelected = { topic, noteText ->
                                viewModel.selectNote(topic, noteText)
                            },
                            onUriSelected = { uri ->
                                viewModel.setNoteUri(uri)
                            },
                            onBrewQuest = {
                                viewModel.brewDailyQuest()
                            }
                        )
                    }

                    Screen.Quiz -> {
                        QuizScreen(
                            quizState = uiState.quizState,
                            onSelectOption = { index ->
                                viewModel.selectQuizOption(index)
                            },
                            onTapPoolChip = { chip ->
                                viewModel.tapPoolChip(chip)
                            },
                            onRemoveFilledBlank = { index ->
                                viewModel.removeFilledBlank(index)
                            },
                            onSelectMatchTerm = { termId ->
                                viewModel.selectMatchTerm(termId)
                            },
                            onSelectMatchDef = { defId ->
                                viewModel.selectMatchDef(defId)
                            },
                            onSpeechRecognized = { text ->
                                viewModel.updateSpokenVoiceText(text)
                            },
                            onSetVoiceListening = { listening ->
                                viewModel.setVoiceListening(listening)
                            },
                            onSubmitAnswer = {
                                viewModel.submitQuizAnswer()
                            },
                            onNextQuestion = {
                                viewModel.nextQuizQuestion()
                            },
                            onRestartQuiz = {
                                viewModel.restartQuiz()
                            },
                            onExitQuiz = {
                                viewModel.navigateTo(Screen.Home)
                            },
                            onRestoreHeartWithAd = {
                                viewModel.restoreHeartFromAdReward()
                            }
                        )
                    }

                    Screen.Leaderboard -> {
                        LeaderboardScreen(
                            currentFilter = uiState.leaderboardFilter,
                            weeklyList = uiState.weeklyLeaderboard,
                            collegeList = uiState.collegeLeaderboard,
                            onFilterChange = { filter ->
                                viewModel.setLeaderboardFilter(filter)
                            }
                        )
                    }

                    Screen.Profile -> {
                        ProfileScreen(
                            userProfile = uiState.userProfile,
                            badges = uiState.badges,
                            onLogout = {
                                viewModel.logout()
                            }
                        )
                    }
                }
            }

            // Floating Bottom Navigation Bar
            if (showBottomBar) {
                FloatingBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
