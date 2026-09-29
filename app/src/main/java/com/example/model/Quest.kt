package com.example.model

data class RevisionQuest(
    val id: String,
    val topicName: String,
    val subject: String,
    val progressPercent: Float, // 0.0 to 1.0
    val totalQuestions: Int,
    val solvedQuestions: Int,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val iconEmoji: String,
    val accentColorHex: Long
)

sealed class QuizQuestion {
    abstract val id: String
    abstract val topic: String
    abstract val explanation: String
    abstract val xpReward: Int

    data class MultipleChoice(
        override val id: String,
        val questionText: String,
        val options: List<String>,
        val correctOptionIndex: Int,
        override val explanation: String,
        override val topic: String,
        val isTrueFalse: Boolean = false,
        override val xpReward: Int = 20
    ) : QuizQuestion()

    data class FillInTheBlanks(
        override val id: String,
        val prompt: String,
        val sentencePrefix: String,
        val blankPlaceholders: List<String>, // Expected words in order
        val sentenceMiddle: String = "",
        val sentenceSuffix: String = "",
        val wordPool: List<String>,          // Scrambled pool of chip tokens
        override val explanation: String,
        override val topic: String,
        override val xpReward: Int = 25
    ) : QuizQuestion()

    data class ConceptMatch(
        override val id: String,
        val prompt: String,
        val pairs: List<MatchPair>,          // 4 matching term <-> definition pairs
        override val explanation: String,
        override val topic: String,
        override val xpReward: Int = 30
    ) : QuizQuestion()

    data class VoiceAnswer(
        override val id: String,
        val questionPrompt: String,
        val requiredKeywords: List<String>,  // Keywords to evaluate in speech recognition
        val sampleModelAnswer: String,
        val minKeywordsRequired: Int = 2,
        override val explanation: String,
        override val topic: String,
        override val xpReward: Int = 35
    ) : QuizQuestion()
}

data class MatchPair(
    val id: String,
    val term: String,
    val definition: String
)

data class NoteUploadItem(
    val id: String,
    val title: String,
    val subject: String,
    val dateString: String,
    val summaryExcerpt: String,
    val sampleImageUrl: String? = null
)
