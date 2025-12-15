package com.example.final_project.ui.tasks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.final_project.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(navController: NavController, viewModel: TaskListViewModel = hiltViewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You have no tasks.\nTap the '+' button to add one!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = tasks, key = { it.task.id }) { taskWithCategory ->
                TaskItem(
                    modifier = Modifier.animateItem(),
                    navController = navController,
                    taskWithCategory = taskWithCategory,
                    onTaskCheckedChange = { isChecked ->
                        viewModel.updateTaskCompletion(taskWithCategory.task, isChecked)
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    taskWithCategory: TaskWithCategory,
    navController: NavController,
    onTaskCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithCategory.task
    val category = taskWithCategory.category

    // Apply a line-through style if the task is completed
    val textStyle = if (task.isCompleted) {
        MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
    } else {
        MaterialTheme.typography.bodyLarge
    }

    Card(modifier = modifier.fillMaxWidth(), onClick = { navController.navigate(Screen.EditTask.createRoute(task.id)) }) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onTaskCheckedChange
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = task.title,
                    style = textStyle,
                    color = if (task.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                )

                // Show the due date only if it exists
                task.dueDate?.let {
                    val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
                    Text(
                        text = "Due: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            category?.let {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = Color(it.colorHex.toULong()),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}
