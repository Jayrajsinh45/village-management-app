package com.villagemanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.villagemanagement.ui.screens.*
import com.villagemanagement.ui.viewmodel.AuthViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val startDestination = when {
        authState.isAuthenticated -> Screen.Home.route
        else -> Screen.Splash.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToVillageList = {
                    navController.navigate(Screen.VillageList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToVillageDetails = { villageId ->
                    navController.navigate(Screen.VillageDetails.createRoute(villageId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Village List Screen
        composable(Screen.VillageList.route) {
            VillageListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVillageDetails = { villageId ->
                    navController.navigate(Screen.VillageDetails.createRoute(villageId))
                },
                onNavigateToAddVillage = {
                    navController.navigate(Screen.AddVillage.route)
                }
            )
        }

        // Village Details Screen
        composable(
            route = Screen.VillageDetails.route,
            arguments = listOf(navArgument("villageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val villageId = backStackEntry.arguments?.getString("villageId") ?: ""
            VillageDetailsScreen(
                villageId = villageId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.createRoute(villageId))
                },
                onNavigateToHelp = {
                    navController.navigate(Screen.HelpSection.createRoute(villageId))
                },
                onNavigateToSuggestions = {
                    navController.navigate(Screen.SuggestionSection.createRoute(villageId))
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminPanel.createRoute(villageId))
                }
            )
        }

        // Add Village Screen
        composable(Screen.AddVillage.route) {
            AddVillageScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Map Screen
        composable(
            route = Screen.Map.route,
            arguments = listOf(navArgument("villageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val villageId = backStackEntry.arguments?.getString("villageId") ?: ""
            MapScreen(
                villageId = villageId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Help Section Screen
        composable(
            route = Screen.HelpSection.route,
            arguments = listOf(navArgument("villageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val villageId = backStackEntry.arguments?.getString("villageId") ?: ""
            HelpSectionScreen(
                villageId = villageId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Suggestion Section Screen
        composable(
            route = Screen.SuggestionSection.route,
            arguments = listOf(navArgument("villageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val villageId = backStackEntry.arguments?.getString("villageId") ?: ""
            SuggestionSectionScreen(
                villageId = villageId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Admin Panel Screen
        composable(
            route = Screen.AdminPanel.route,
            arguments = listOf(navArgument("villageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val villageId = backStackEntry.arguments?.getString("villageId") ?: ""
            AdminPanelScreen(
                villageId = villageId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
