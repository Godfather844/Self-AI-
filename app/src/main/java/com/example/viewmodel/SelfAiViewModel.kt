package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DemoData
import com.example.data.GeminiQuestService
import com.example.data.db.AppDatabase
import com.example.data.db.AuthRepository
import com.example.model.BadgeItem
import com.example.model.LeaderboardEntry
import com.example.model.LeaderboardFilter
import com.example.model.QuizQuestion
import com.example.model.RevisionQuest
import com.example.model.UserProfile
import com.example.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val heartsRemaining: Int = 3,
    val totalXpEarned: Int = 0,
    val correctCount: Int = 0,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val isCompleted: Boolean = false,
    val isFailed: Boolean = false,

    // MultipleChoice / TrueFalse State
    val selectedOptionIndex: Int? = null,

    // FillInTheBlanks State
    val filledBlanks: List<String> = emptyList(),
    val availablePool: List<String> = emptyList(),

    // ConceptMatch State
    val matchedPairIds: Set<String> = emptySet(),
    val selectedTermId: String? = null,
    val selectedDefId: String? = null,
    val isPairMismatch: Boolean = false,

    // VoiceAnswer State
    val spokenText: String = "",
    val detectedKeywords: Set<String> = emptySet(),
    val isListening: Boolean = false
)

data class SelfAiUiState(
    val currentScreen: Screen = Screen.Home,
    val previousScreen: Screen = Screen.Home,
    val isLoggedIn: Boolean = true,
    val isLoadingAuth: Boolean = false,
    val authErrorMessage: String? = null,
    val userProfile: UserProfile = UserProfile(),
    val quests: List<RevisionQuest> = DemoData.sampleQuests,
    val weeklyLeaderboard: List<LeaderboardEntry> = emptyList(),
    val collegeLeaderboard: List<LeaderboardEntry> = emptyList(),
    val leaderboardFilter: LeaderboardFilter = LeaderboardFilter.WEEKLY,
    val badges: List<BadgeItem> = DemoData.userBadges,
    // Note Upload State
    val selectedNoteUri: Uri? = null,
    val selectedTopicName: String = "DBMS Normalization",
    val selectedNoteText: String = DemoData.sampleNoteTexts[0],
    val isBrewing: Boolean = false,
    // Active Quiz State
    val quizState: QuizUiState = QuizUiState()
)

class SelfAiViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepository(AppDatabase.getInstance(application), application)
    private val geminiService = GeminiQuestService()

    private val _uiState = MutableStateFlow(SelfAiUiState())
    val uiState: StateFlow<SelfAiUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.cleanDummySeedUserIfPresent()
            authRepository.activeUserFlow.collect { userEntity ->
                if (userEntity != null) {
                    val profile = UserProfile(
                        name = userEntity.name,
                        email = userEntity.email,
                        college = userEntity.college,
                        branch = userEntity.branch,
                        levelTitle = userEntity.levelTitle,
                        avatarEmoji = userEntity.avatarEmoji,
                        xp = userEntity.xp,
                        currentStreak = userEntity.currentStreak,
                        longestStreak = userEntity.longestStreak,
                        totalQuestsSolved = userEntity.totalQuestsSolved,
                        accuracyPercentage = userEntity.accuracyPercentage,
                        heartsRemaining = userEntity.heartsRemaining
                    )
                    _uiState.update { current ->
                        current.copy(
                            isLoggedIn = true,
                            userProfile = profile
                        )
                    }

                    // Setup real leaderboard strictly from registered database & Firestore users
                    launch {
                        authRepository.getAllRegisteredUsersFlow().collect { allDbUsers ->
                            val firestoreUsers = try {
                                authRepository.fetchFirestoreLeaderboardUsers()
                            } catch (e: Exception) {
                                emptyList()
                            }
                            val otherRealUsers = (allDbUsers + firestoreUsers)
                                .filter { it.email.lowercase() != userEntity.email.lowercase() }
                                .distinctBy { it.email.lowercase() }
                                .map { u ->
                                    LeaderboardEntry(
                                        rank = 0,
                                        name = u.name,
                                        avatarEmoji = u.avatarEmoji,
                                        college = u.college,
                                        xp = u.xp,
                                        streak = u.currentStreak,
                                        badgeLabel = if (u.xp >= 300) "Mastermind" else "Scholar",
                                        isCurrentUser = false
                                    )
                                }

                            val (dynamicWeekly, dynamicCollege) = DemoData.getDynamicLeaderboards(profile, otherRealUsers)
                            _uiState.update { current ->
                                current.copy(
                                    weeklyLeaderboard = dynamicWeekly,
                                    collegeLeaderboard = dynamicCollege
                                )
                            }
                        }
                    }

                    // Collect real quest history for the logged-in student
                    launch {
                        authRepository.getQuestHistoryFlow(userEntity.email).collect { history ->
                            val realQuests = if (history.isNotEmpty()) {
                                history.map { record ->
                                    val percent = (record.scoreEarned.toFloat() / record.totalQuestions.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                    RevisionQuest(
                                        id = "quest_${record.id}",
                                        topicName = record.topicName,
                                        subject = when {
                                            record.topicName.contains("DBMS", true) || record.topicName.contains("SQL", true) -> "Database Systems"
                                            record.topicName.contains("OS", true) || record.topicName.contains("Semaphore", true) -> "Operating Systems"
                                            record.topicName.contains("Network", true) || record.topicName.contains("TCP", true) -> "Computer Networks"
                                            else -> "College Notes & Revision"
                                        },
                                        progressPercent = percent,
                                        totalQuestions = record.totalQuestions,
                                        solvedQuestions = record.scoreEarned,
                                        difficulty = if (percent >= 1f) "Mastered" else if (percent >= 0.7f) "Good Progress" else "Needs Review",
                                        iconEmoji = when {
                                            record.topicName.contains("DBMS", true) -> "🗄️"
                                            record.topicName.contains("OS", true) -> "🚦"
                                            record.topicName.contains("Network", true) -> "🌐"
                                            else -> "📚"
                                        },
                                        accentColorHex = when {
                                            record.topicName.contains("DBMS", true) -> 0xFF0284C7
                                            record.topicName.contains("OS", true) -> 0xFF8B5CF6
                                            record.topicName.contains("Network", true) -> 0xFFFB923C
                                            else -> 0xFF10B981
                                        }
                                    )
                                }
                            } else {
                                // Default curriculum topics with 0% progress before student completes quizzes
                                DemoData.sampleQuests
                            }
                            _uiState.update { state -> state.copy(quests = realQuests) }
                        }
                    }
                } else {
                    _uiState.update { current ->
                        current.copy(
                            isLoggedIn = false,
                            currentScreen = Screen.Auth,
                            userProfile = UserProfile(name = "", email = "", college = "", branch = "", xp = 0, currentStreak = 0),
                            weeklyLeaderboard = emptyList(),
                            collegeLeaderboard = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { current ->
            current.copy(
                previousScreen = current.currentScreen,
                currentScreen = screen
            )
        }
    }

    fun handleBack() {
        val current = _uiState.value.currentScreen
        if (current == Screen.Quiz) {
            navigateTo(Screen.PlayUpload)
        } else if (current != Screen.Home) {
            navigateTo(Screen.Home)
        }
    }

    fun login(email: String, password: String = "password123", college: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAuth = true, authErrorMessage = null) }
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoadingAuth = false, currentScreen = Screen.Home, isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAuth = false,
                        authErrorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun signUp(name: String, email: String, password: String, college: String, branch: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAuth = true, authErrorMessage = null) }
            val result = authRepository.signUp(name, email, password, college, branch)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoadingAuth = false, currentScreen = Screen.Home, isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAuth = false,
                        authErrorMessage = result.exceptionOrNull()?.message ?: "Sign up failed"
                    )
                }
            }
        }
    }

    fun checkGoogleAccount(email: String, onResult: (isRegistered: Boolean) -> Unit) {
        viewModelScope.launch {
            val registered = authRepository.isUserRegistered(email)
            onResult(registered)
        }
    }

    fun directGoogleLogin(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAuth = true, authErrorMessage = null) }
            val result = authRepository.directGoogleLogin(email)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoadingAuth = false, currentScreen = Screen.Home, isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAuth = false,
                        authErrorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    )
                }
            }
        }
    }

    fun completeGoogleRegistration(
        email: String,
        name: String,
        college: String,
        branch: String,
        avatarEmoji: String = "🧑‍💻"
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAuth = true, authErrorMessage = null) }
            val result = authRepository.registerGoogleStudent(
                email = email,
                name = name,
                college = college,
                branch = branch,
                avatarEmoji = avatarEmoji
            )
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoadingAuth = false, currentScreen = Screen.Home, isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAuth = false,
                        authErrorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun signInWithGoogle(
        googleEmail: String,
        displayName: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAuth = true, authErrorMessage = null) }
            val result = authRepository.signInWithGoogleAccount(googleEmail, displayName)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoadingAuth = false, currentScreen = Screen.Home, isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAuth = false,
                        authErrorMessage = result.exceptionOrNull()?.message ?: "Google sign-in failed"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { current ->
                current.copy(
                    isLoggedIn = false,
                    currentScreen = Screen.Auth
                )
            }
        }
    }

    fun setLeaderboardFilter(filter: LeaderboardFilter) {
        _uiState.update { it.copy(leaderboardFilter = filter) }
    }

    fun selectNote(topicName: String, noteText: String, uri: Uri? = null) {
        _uiState.update {
            it.copy(
                selectedTopicName = topicName,
                selectedNoteText = noteText,
                selectedNoteUri = uri
            )
        }
    }

    fun setNoteUri(uri: Uri?) {
        _uiState.update { it.copy(selectedNoteUri = uri) }
    }

    fun setQuizQuestions(questions: List<QuizQuestion>) {
        val firstQ = questions.firstOrNull()
        val pool = if (firstQ is QuizQuestion.FillInTheBlanks) firstQ.wordPool else emptyList()
        _uiState.update { state ->
            state.copy(
                quizState = QuizUiState(
                    questions = questions,
                    availablePool = pool
                )
            )
        }
    }

    fun brewDailyQuest() {
        val current = _uiState.value
        _uiState.update { it.copy(isBrewing = true) }

        viewModelScope.launch {
            val questions = geminiService.brewQuestFromNote(
                noteText = current.selectedNoteText,
                topicName = current.selectedTopicName
            )

            val initialQuestion = questions.firstOrNull()
            val initialPool = if (initialQuestion is QuizQuestion.FillInTheBlanks) {
                initialQuestion.wordPool
            } else emptyList()

            _uiState.update { state ->
                state.copy(
                    isBrewing = false,
                    currentScreen = Screen.Quiz,
                    quizState = QuizUiState(
                        questions = questions,
                        currentQuestionIndex = 0,
                        heartsRemaining = 3,
                        totalXpEarned = 0,
                        correctCount = 0,
                        isAnswerSubmitted = false,
                        isCompleted = false,
                        isFailed = false,
                        availablePool = initialPool,
                        filledBlanks = emptyList(),
                        matchedPairIds = emptySet()
                    )
                )
            }
        }
    }

    // 1. MultipleChoice / QuickTrueFalse Option
    fun selectQuizOption(index: Int) {
        if (_uiState.value.quizState.isAnswerSubmitted) return
        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(selectedOptionIndex = index)
            )
        }
    }

    // 2. FillInTheBlanks interaction
    fun tapPoolChip(chip: String) {
        val quiz = _uiState.value.quizState
        if (quiz.isAnswerSubmitted) return
        val currentQ = quiz.questions.getOrNull(quiz.currentQuestionIndex) as? QuizQuestion.FillInTheBlanks ?: return
        if (quiz.filledBlanks.size >= currentQ.blankPlaceholders.size) return

        val newPool = quiz.availablePool.toMutableList()
        newPool.remove(chip)
        val newFilled = quiz.filledBlanks + chip

        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(
                    availablePool = newPool,
                    filledBlanks = newFilled
                )
            )
        }
    }

    fun removeFilledBlank(index: Int) {
        val quiz = _uiState.value.quizState
        if (quiz.isAnswerSubmitted) return
        if (index !in quiz.filledBlanks.indices) return

        val removedChip = quiz.filledBlanks[index]
        val newFilled = quiz.filledBlanks.toMutableList().apply { removeAt(index) }
        val newPool = quiz.availablePool + removedChip

        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(
                    availablePool = newPool,
                    filledBlanks = newFilled
                )
            )
        }
    }

    // 3. ConceptMatch interaction
    fun selectMatchTerm(pairId: String) {
        val quiz = _uiState.value.quizState
        if (quiz.isAnswerSubmitted) return
        if (quiz.matchedPairIds.contains(pairId)) return

        val defId = quiz.selectedDefId
        if (defId != null) {
            evaluatePairMatch(termId = pairId, defId = defId)
        } else {
            _uiState.update { state ->
                state.copy(
                    quizState = state.quizState.copy(
                        selectedTermId = pairId,
                        isPairMismatch = false
                    )
                )
            }
        }
    }

    fun selectMatchDef(pairId: String) {
        val quiz = _uiState.value.quizState
        if (quiz.isAnswerSubmitted) return
        if (quiz.matchedPairIds.contains(pairId)) return

        val termId = quiz.selectedTermId
        if (termId != null) {
            evaluatePairMatch(termId = termId, defId = pairId)
        } else {
            _uiState.update { state ->
                state.copy(
                    quizState = state.quizState.copy(
                        selectedDefId = pairId,
                        isPairMismatch = false
                    )
                )
            }
        }
    }

    private fun evaluatePairMatch(termId: String, defId: String) {
        if (termId == defId) {
            val quiz = _uiState.value.quizState
            val currentQ = quiz.questions.getOrNull(quiz.currentQuestionIndex) as? QuizQuestion.ConceptMatch
            val newMatched = quiz.matchedPairIds + termId
            val allMatched = currentQ != null && newMatched.size >= currentQ.pairs.size

            _uiState.update { state ->
                state.copy(
                    quizState = state.quizState.copy(
                        matchedPairIds = newMatched,
                        selectedTermId = null,
                        selectedDefId = null,
                        isPairMismatch = false
                    )
                )
            }

            if (allMatched) {
                submitQuizAnswer()
            }
        } else {
            _uiState.update { state ->
                state.copy(
                    quizState = state.quizState.copy(
                        selectedTermId = termId,
                        selectedDefId = defId,
                        isPairMismatch = true
                    )
                )
            }
            viewModelScope.launch {
                delay(700)
                _uiState.update { state ->
                    state.copy(
                        quizState = state.quizState.copy(
                            selectedTermId = null,
                            selectedDefId = null,
                            isPairMismatch = false
                        )
                    )
                }
            }
        }
    }

    // 4. VoiceAnswer interaction
    fun setVoiceListening(listening: Boolean) {
        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(isListening = listening)
            )
        }
    }

    fun updateSpokenVoiceText(text: String) {
        val quiz = _uiState.value.quizState
        val currentQ = quiz.questions.getOrNull(quiz.currentQuestionIndex) as? QuizQuestion.VoiceAnswer
        val matched = if (currentQ != null) {
            currentQ.requiredKeywords.filter { keyword ->
                text.contains(keyword, ignoreCase = true)
            }.toSet()
        } else emptySet()

        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(
                    spokenText = text,
                    detectedKeywords = matched
                )
            )
        }
    }

    // Universal Submit Answer based on question type
    fun submitQuizAnswer() {
        val quiz = _uiState.value.quizState
        if (quiz.isAnswerSubmitted) return
        val currentQ = quiz.questions.getOrNull(quiz.currentQuestionIndex) ?: return

        val isCorrect = when (currentQ) {
            is QuizQuestion.MultipleChoice -> {
                val selected = quiz.selectedOptionIndex ?: return
                selected == currentQ.correctOptionIndex
            }
            is QuizQuestion.FillInTheBlanks -> {
                if (quiz.filledBlanks.size < currentQ.blankPlaceholders.size) return
                quiz.filledBlanks == currentQ.blankPlaceholders
            }
            is QuizQuestion.ConceptMatch -> {
                quiz.matchedPairIds.size >= currentQ.pairs.size
            }
            is QuizQuestion.VoiceAnswer -> {
                quiz.detectedKeywords.size >= currentQ.minKeywordsRequired ||
                        quiz.spokenText.length >= 15
            }
        }

        val newHearts = if (isCorrect) quiz.heartsRemaining else (quiz.heartsRemaining - 1).coerceAtLeast(0)
        val addedXp = if (isCorrect) currentQ.xpReward else 0
        val newCorrectCount = if (isCorrect) quiz.correctCount + 1 else quiz.correctCount
        val isFailed = newHearts <= 0

        _uiState.update { state ->
            state.copy(
                quizState = state.quizState.copy(
                    isAnswerSubmitted = true,
                    isCorrect = isCorrect,
                    heartsRemaining = newHearts,
                    totalXpEarned = quiz.totalXpEarned + addedXp,
                    correctCount = newCorrectCount,
                    isFailed = isFailed
                )
            )
        }
    }

    fun nextQuizQuestion() {
        val quiz = _uiState.value.quizState
        val nextIndex = quiz.currentQuestionIndex + 1

        if (nextIndex >= quiz.questions.size) {
            // Quiz completed! Award XP to Room DB and profile
            val earnedXp = quiz.totalXpEarned
            val userEmail = _uiState.value.userProfile.email
            val topic = _uiState.value.selectedTopicName

            viewModelScope.launch {
                authRepository.recordQuestCompletion(
                    userEmail = userEmail,
                    topicName = topic,
                    scoreEarned = quiz.correctCount,
                    totalQuestions = quiz.questions.size,
                    xpEarned = earnedXp
                )
            }

            _uiState.update { state ->
                val updatedProfile = state.userProfile.copy(
                    xp = state.userProfile.xp + earnedXp,
                    totalQuestsSolved = state.userProfile.totalQuestsSolved + 1,
                    currentStreak = state.userProfile.currentStreak + 1
                )
                val (newWeekly, newCollege) = DemoData.getDynamicLeaderboards(updatedProfile)
                state.copy(
                    userProfile = updatedProfile,
                    weeklyLeaderboard = newWeekly,
                    collegeLeaderboard = newCollege,
                    quizState = state.quizState.copy(
                        isCompleted = true,
                        isAnswerSubmitted = false
                    )
                )
            }
        } else {
            val nextQ = quiz.questions.getOrNull(nextIndex)
            val pool = if (nextQ is QuizQuestion.FillInTheBlanks) nextQ.wordPool else emptyList()

            _uiState.update { state ->
                state.copy(
                    quizState = state.quizState.copy(
                        currentQuestionIndex = nextIndex,
                        selectedOptionIndex = null,
                        isAnswerSubmitted = false,
                        availablePool = pool,
                        filledBlanks = emptyList(),
                        matchedPairIds = emptySet(),
                        selectedTermId = null,
                        selectedDefId = null,
                        isPairMismatch = false,
                        spokenText = "",
                        detectedKeywords = emptySet(),
                        isListening = false
                    )
                )
            }
        }
    }

    fun restartQuiz() {
        val questions = _uiState.value.quizState.questions
        val firstQ = questions.firstOrNull()
        val pool = if (firstQ is QuizQuestion.FillInTheBlanks) firstQ.wordPool else emptyList()

        _uiState.update { state ->
            state.copy(
                quizState = QuizUiState(
                    questions = questions,
                    currentQuestionIndex = 0,
                    selectedOptionIndex = null,
                    isAnswerSubmitted = false,
                    heartsRemaining = 3,
                    totalXpEarned = 0,
                    correctCount = 0,
                    isCompleted = false,
                    isFailed = false,
                    availablePool = pool,
                    filledBlanks = emptyList(),
                    matchedPairIds = emptySet()
                )
            )
        }
    }

    fun restoreHeartFromAdReward() {
        val currentQuiz = _uiState.value.quizState
        val newHearts = (currentQuiz.heartsRemaining + 1).coerceAtMost(3)
        val userEmail = _uiState.value.userProfile.email

        viewModelScope.launch {
            authRepository.updateUserHearts(userEmail, newHearts)
        }

        _uiState.update { state ->
            val updatedUser = state.userProfile.copy(heartsRemaining = newHearts)
            state.copy(
                userProfile = updatedUser,
                quizState = state.quizState.copy(
                    heartsRemaining = newHearts,
                    isFailed = false,
                    isAnswerSubmitted = false
                )
            )
        }
    }
}
