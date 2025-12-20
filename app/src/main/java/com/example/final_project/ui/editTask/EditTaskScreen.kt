package com.example.final_project.ui.editTask

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.final_project.ui.components.NewCategoryDialog
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavController,
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    // State for the DatePickerDialog
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Show the New Category Dialog when the ViewModel state is true
    if (viewModel.showNewCategoryDialog) {
        NewCategoryDialog(
            onDismissRequest = { viewModel.onNewCategoryDialogDismiss() },
            onConfirm = { name, color ->
                viewModel.createNewCategory(name, color)
            }
        )
    }

    // Show the Date Picker Dialog when the state is true
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            val instant = Instant.ofEpochMilli(utcMillis)
                            val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
                            val localMillis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            viewModel.onDueDateChange(localMillis)
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.updateTask {
                                navController.popBackStack()
                            }
                        },
                        // Enable button only if the title is not blank
                        enabled = viewModel.taskTitle.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Title
            OutlinedTextField(
                value = viewModel.taskTitle,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Due Date
            OutlinedTextField(
                value = viewModel.taskDueDate?.let { localMillis ->
                    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).apply {
                        timeZone = TimeZone.getDefault()
                    }
                    formatter.format(Date(localMillis))
                } ?: "No Due Date",
                onValueChange = {},
                readOnly = true,
                label = { Text("Due Date (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == viewModel.selectedCategoryId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    // "Add New..." item
                    DropdownMenuItem(
                        text = { Text("+ Add New Category", color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            isCategoryDropdownExpanded = false
                            viewModel.onAddNewCategoryClicked()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("No Category") },
                        onClick = {
                            viewModel.onCategorySelected(null)
                            isCategoryDropdownExpanded = false
                        }
                    )
                    // Category list
                    categories.forEach { category ->
                        Log.d("ColorDebug", "Category: '${category.name}' with color: '${category.colorHex}'")
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                viewModel.onCategorySelected(category.id)
                                isCategoryDropdownExpanded = false
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(color = Color(category.colorHex.toULong()), shape = CircleShape)
                                )
                            }
                        )
                    }
                }
            }

            // Task Notes
            OutlinedTextField(
                value = viewModel.taskNotes,
                onValueChange = { viewModel.onNotesChange(it) },
                label = { Text("Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Takes up remaining space
                maxLines = 10
            )
        }
    }
}
