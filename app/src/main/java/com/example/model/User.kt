package com.example.model

data class UserProfile(
    val name: String = "Lucky Kushwaha",
    val email: String = "kushwahalucky939@gmail.com",
    val college: String = "National Institute of Technology",
    val branch: String = "Computer Science & Engineering",
    val levelTitle: String = "Scholar - Lvl 1",
    val avatarEmoji: String = "🚀",
    val xp: Int = 120,
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val totalQuestsSolved: Int = 0,
    val accuracyPercentage: Int = 100,
    val heartsRemaining: Int = 3
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val dateUnlocked: String? = null
)
