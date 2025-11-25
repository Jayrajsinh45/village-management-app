package com.villagemanagement.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object VillageList : Screen("village_list")
    object VillageDetails : Screen("village_details/{villageId}") {
        fun createRoute(villageId: String) = "village_details/$villageId"
    }
    object AddVillage : Screen("add_village")
    object Map : Screen("map/{villageId}") {
        fun createRoute(villageId: String) = "map/$villageId"
    }
    object HelpSection : Screen("help_section/{villageId}") {
        fun createRoute(villageId: String) = "help_section/$villageId"
    }
    object SuggestionSection : Screen("suggestion_section/{villageId}") {
        fun createRoute(villageId: String) = "suggestion_section/$villageId"
    }
    object Profile : Screen("profile")
    object AdminPanel : Screen("admin_panel/{villageId}") {
        fun createRoute(villageId: String) = "admin_panel/$villageId"
    }
}
