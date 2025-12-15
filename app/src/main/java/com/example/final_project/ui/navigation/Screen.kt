package com.example.final_project.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object SignUp : Screen("signup_screen")
    object Home : Screen("home_screen")
    object Profile : Screen("profile_screen")
    object Calendar : Screen("calendar_screen")
    object Chat : Screen("chat_screen")
    object Settings : Screen("settings_screen")
    object NewTask : Screen("new_task_screen")
    object EditTask : Screen("edit_task_screen/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task_screen/$taskId"
    }
}