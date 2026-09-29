package com.example.model

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val college: String,
    val xp: Int,
    val streak: Int,
    val badgeLabel: String,
    val isCurrentUser: Boolean = false
)

enum class LeaderboardFilter {
    WEEKLY,
    COLLEGE_BATCHMATES
}
