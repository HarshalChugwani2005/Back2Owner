package com.back2owner.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.back2owner.app.ui.navigation.Back2OwnerNavigation
import com.back2owner.app.ui.viewmodel.AuthViewModel

/**
 * Main application entry point with Scaffold hosting bottom navigation.
 * Implements an authentication gate to ensure only logged-in users see the main app.
 */
@Composable
fun Back2OwnerApp(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // Track current route to hide bottom bar on auth screens
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = when (currentRoute) {
        "login", "signup" -> false
        null -> false
        else -> isLoggedIn == true
    }

    // Auth Guard Force Navigation
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        } else if (isLoggedIn == true && (currentRoute == "login" || currentRoute == "signup" || currentRoute == null)) {
            navController.navigate("feed") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Back2OwnerBottomNavBar(navController, currentRoute)
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoggedIn == null) {
                // Initial auth check state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Back2OwnerNavigation(navController, authViewModel)
            }
        }
    }
}

@Composable
private fun Back2OwnerBottomNavBar(navController: NavController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem("feed", "Feed", Icons.Default.Home),
        BottomNavItem("report", "Report", Icons.Default.AddCircle),
        BottomNavItem("profile", "Profile", Icons.Default.Person),
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)
