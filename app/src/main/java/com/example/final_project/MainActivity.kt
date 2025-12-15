package com.example.final_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.final_project.ui.MainViewModel
import com.example.final_project.ui.auth.LoginScreen
import com.example.final_project.ui.auth.SignUpScreen
import com.example.final_project.ui.chat.ChatScreen
import com.example.final_project.ui.editTask.EditTaskScreen
import com.example.final_project.ui.home.HomeScreen
import com.example.final_project.ui.newTask.NewTaskScreen
import com.example.final_project.ui.theme.FinalProjectTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.final_project.ui.navigation.Screen
import com.example.final_project.ui.profile.ProfileScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProjectTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val startDestination by viewModel.startDestination.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController = navController, )
                        }

                        composable(Screen.SignUp.route) {
                            SignUpScreen(navController = navController)
                        }
                        composable(Screen.Home.route) {
                            HomeScreen(navController = navController)
                        }
                        composable(Screen.Profile.route) {
                            ProfileScreen(navController = navController)
                        }
                        composable(Screen.NewTask.route) {
                            NewTaskScreen(navController = navController)
                        }
                        composable(route = Screen.EditTask.route, arguments = listOf(navArgument("taskId") { type = NavType.IntType })) {
                            EditTaskScreen(navController = navController)
                        }
                        composable(Screen.Chat.route) {
                            ChatScreen(navController = navController)
                        }

                    }
                }
            }
        }
    }
}