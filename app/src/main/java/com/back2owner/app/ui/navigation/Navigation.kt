package com.back2owner.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.back2owner.app.ui.screens.*

/**
 * Main navigation graph for the Back2Owner application.
 * Defines all screen routes and provides common navigation callbacks.
 */
@Composable
fun Back2OwnerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "feed" // Default destination for authenticated users
    ) {
        // Authentication Screens
        composable("login") {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Main App Screens
        composable("feed") {
            FeedScreen(
                onItemClick = { itemId -> 
                    navController.navigate("itemDetail/$itemId") 
                }
            )
        }
        composable("report") {
            ReportItemScreen(
                onSuccess = { 
                    navController.navigate("feed") {
                        popUpTo("report") { inclusive = true }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        // Flow Screens
        composable(
            route = "itemDetail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailScreen(
                itemId = itemId,
                onBackClick = { navController.popBackStack() },
                onClaimClick = { id -> navController.navigate("claim/$id") }
            )
        }
        
        composable(
            route = "claim/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ClaimScreen(
                itemId = itemId,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("feed") {
                        popUpTo("feed") { inclusive = false }
                    }
                }
            )
        }
    }
}
