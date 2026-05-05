package com.fhnav.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fhnav.app.ui.auth.LoginScreen
import com.fhnav.app.ui.auth.NicknameScreen
import com.fhnav.app.ui.auth.VerifyCodeScreen
import com.fhnav.app.ui.map.MapScreen
import com.fhnav.app.ui.navigation_ui.NavigationScreen
import com.fhnav.app.ui.settings.AboutScreen
import com.fhnav.app.ui.settings.SettingsScreen
import com.fhnav.app.ui.social.FriendListScreen
import com.fhnav.app.ui.welcome.WelcomeScreen

@Composable
fun FHNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToVerify = { phone ->
                    navController.navigate(Screen.VerifyCode.createRoute(phone))
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            VerifyCodeScreen(
                phone = phone,
                onNavigateToNickname = {
                    navController.navigate(Screen.Nickname.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Nickname.route) {
            NicknameScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToNavigation = { name, destLat, destLng, originLat, originLng ->
                    navController.navigate(
                        Screen.Navigation.createRoute(name, destLat, destLng, originLat, originLng)
                    )
                },
                onNavigateToFriends = {
                    navController.navigate(Screen.Friends.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Navigation.route,
            arguments = listOf(
                navArgument("destinationName") { type = NavType.StringType },
                navArgument("destLat") { type = NavType.FloatType },
                navArgument("destLng") { type = NavType.FloatType },
                navArgument("originLat") { type = NavType.FloatType },
                navArgument("originLng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val destinationName = backStackEntry.arguments?.getString("destinationName") ?: ""
            val destLat = backStackEntry.arguments?.getFloat("destLat")?.toDouble() ?: 0.0
            val destLng = backStackEntry.arguments?.getFloat("destLng")?.toDouble() ?: 0.0
            val originLat = backStackEntry.arguments?.getFloat("originLat")?.toDouble() ?: 0.0
            val originLng = backStackEntry.arguments?.getFloat("originLng")?.toDouble() ?: 0.0

            NavigationScreen(
                destinationName = destinationName,
                destLat = destLat,
                destLng = destLng,
                originLat = originLat,
                originLng = originLng,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Friends.route) {
            FriendListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
