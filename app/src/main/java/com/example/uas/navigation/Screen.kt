package com.example.uas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Booking : Screen("booking")
    data object History : Screen("history")
    data object Profile : Screen("profile")
}

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Booking, "Booking", Icons.Filled.DateRange),
    BottomNavItem(Screen.History, "History", Icons.Filled.List),
    BottomNavItem(Screen.Profile, "Profile", Icons.Filled.Person)
)
