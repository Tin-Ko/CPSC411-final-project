package com.example.final_project.ui.newTask

import androidx.compose.foundation.background
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.final_project.ui.components.NewCategoryDialog
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    navController: NavController,
    viewModel: NewTaskViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val showNewCategoryDialog = viewModel.showNewCategoryDialog
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    if (showNewCategoryDialog) {
        NewCategoryDialog(
            onDismissRequest = { viewModel.onNewCategoryDialogDismiss() },
            onConfirm = { name, color ->
                viewModel.createNewCategory(name, color)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Task") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveTask {
                    navController.popBackStack()
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save Task")
            }
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
                onValueChange = viewModel::onTitleChange,
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Due Date Picker
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
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Picker
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCategoryName = categories.find { it.id == viewModel.selectedCategoryId }?.name ?: "No Category"
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor() // This is important
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    // "Add New" option
                    DropdownMenuItem(
                        text = { Text("+ Add New Category") },
                        onClick = {
                            isCategoryDropdownExpanded = false
                            viewModel.onAddNewCategoryClicked()
                        }
                    )

                    // "No Category" option
                    DropdownMenuItem(
                        text = { Text("No Category") },
                        onClick = {
                            viewModel.onCategorySelected(null)
                            isCategoryDropdownExpanded = false
                        }
                    )

                    // List of existing categories
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                viewModel.onCategorySelected(category.id)
                                isCategoryDropdownExpanded = false
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(Color(category.colorHex.toULong()))
                                )
                            }
                        )
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = viewModel.taskNotes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Add more fields here (Category, Priority, etc.) as needed
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            val instant = Instant.ofEpochMilli(utcMillis)
                            val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
                            val localMillis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            viewModel.onDueDateChange(localMillis)
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
