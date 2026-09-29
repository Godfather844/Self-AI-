package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val passwordHash: String,
    val name: String,
    val college: String,
    val branch: String,
    val levelTitle: String = "Quantum Scholar - Lvl 4",
    val avatarEmoji: String = "🧑‍💻",
    val xp: Int = 120,
    val currentStreak: Int = 3,
    val longestStreak: Int = 7,
    val totalQuestsSolved: Int = 18,
    val accuracyPercentage: Int = 92,
    val heartsRemaining: Int = 3,
    val isLoggedIn: Boolean = true,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quest_history")
data class QuestHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val topicName: String,
    val scoreEarned: Int,
    val totalQuestions: Int,
    val xpEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)
