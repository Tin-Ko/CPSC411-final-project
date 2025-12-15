package com.example.final_project.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.final_project.data.local.TaskWithCategory
import com.example.final_project.ui.calendar.CalendarScreen
import com.example.final_project.ui.chat.ChatScreen
import com.example.final_project.ui.navigation.Screen
import com.example.final_project.ui.tasks.TaskListScreen
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val topBarTitle by viewModel.topBarTitle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(48.dp))
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.Tasks,
                    onClick = { viewModel.onTabSelected(HomeTab.Tasks) },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.Calendar,
                    onClick = { viewModel.onTabSelected(HomeTab.Calendar) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.Chat,
                    onClick = { viewModel.onTabSelected(HomeTab.Chat) },
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = "Chat") },
                    label = { Text("Chat") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                HomeTab.Tasks -> {
                    viewModel.updateTopBarTitle("My Tasks")
                    TaskListScreen(navController = navController)
                }
                HomeTab.Calendar -> {
                    viewModel.updateTopBarTitle("Calendar")
                    CalendarScreen(navController = navController)
                }
                HomeTab.Chat -> {
                    viewModel.updateTopBarTitle("Chat")
                    ChatScreen(navController = navController)
                }
            }
        }
    }
}
