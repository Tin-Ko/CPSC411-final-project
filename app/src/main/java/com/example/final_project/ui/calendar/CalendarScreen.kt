package com.example.final_project.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.final_project.ui.components.CalendarDay
import com.example.final_project.ui.components.MonthCalendar
import com.example.final_project.ui.components.rememberCalendarState
import com.example.final_project.ui.tasks.TaskItem
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val tasksByDate by viewModel.tasksByDate.collectAsState()
    val calendarState = rememberCalendarState()
    val selectedDate by remember {
        derivedStateOf {
            // Normalize the selected date to the start of the day
            val cal = Calendar.getInstance().apply {
                timeInMillis = calendarState.selectedDateMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        }
    }

    val tasksForSelectedDate = tasksByDate[selectedDate] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MonthCalendar(
            state = calendarState,
            modifier = Modifier.padding(horizontal = 8.dp),
            dayContent = { dayCalendar, isSelected ->
                val dateMillis = dayCalendar.timeInMillis
                val hasTasks = tasksByDate.containsKey(dateMillis)

                // Re-creating the CalendarDay content here
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                        .clickable { calendarState.selectedDateMillis = dateMillis },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayCalendar.get(Calendar.DAY_OF_MONTH).toString(),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = contentColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (hasTasks) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(6.dp)) // Keep alignment consistent
                        }
                    }
                }
            }
        )

        if (tasksForSelectedDate.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = tasksForSelectedDate, key = { it.task.id }) { taskWithCategory ->
                    TaskItem(
                        modifier = Modifier.animateItem(),
                        taskWithCategory = taskWithCategory,
                        navController = navController,
                        onTaskCheckedChange = { isChecked ->
                            viewModel.updateTaskCompletion(taskWithCategory.task, isChecked)
                        },
                        onDeleteClick = { viewModel.deleteTask(taskWithCategory.task) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tasks due on this day.")
            }
        }
    }
}
