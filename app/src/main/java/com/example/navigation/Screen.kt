package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    object Auth : Screen("auth", "Authentication")
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object PlayUpload : Screen("play_upload", "Play", Icons.Rounded.PlayArrow)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Rounded.Leaderboard)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)
    object Quiz : Screen("quiz", "Quiz Loop")
}
