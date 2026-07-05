package com.example.uas.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uas.screens.BookingScreen
import com.example.uas.screens.HistoryScreen
import com.example.uas.screens.LoginScreen
import com.example.uas.screens.ProfileScreen
import com.example.uas.screens.RegisterScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uas.viewmodel.AuthViewModel
import com.example.uas.viewmodel.BookingViewModel

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel = viewModel { AuthViewModel() },
    bookingViewModel: BookingViewModel = viewModel { BookingViewModel() }
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Booking.route,
        Screen.History.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Booking.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Booking.route) {
                BookingScreen(
                    authViewModel = authViewModel,
                    bookingViewModel = bookingViewModel,
                    onBookingSuccess = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Booking.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(bookingViewModel = bookingViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
