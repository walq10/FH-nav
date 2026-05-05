package com.fhnav.app.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object VerifyCode : Screen("verify_code/{phone}") {
        fun createRoute(phone: String) = "verify_code/$phone"
    }
    data object Nickname : Screen("nickname")
    data object Map : Screen("map")
    data object Navigation : Screen("navigation/{destinationName}/{destLat}/{destLng}/{originLat}/{originLng}") {
        fun createRoute(
            destinationName: String,
            destLat: Double,
            destLng: Double,
            originLat: Double,
            originLng: Double
        ) = "navigation/$destinationName/$destLat/$destLng/$originLat/$originLng"
    }
    data object Friends : Screen("friends")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}
