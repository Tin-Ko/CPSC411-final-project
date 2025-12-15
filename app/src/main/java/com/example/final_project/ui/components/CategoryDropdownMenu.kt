package com.example.final_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.final_project.data.local.CategoryEntity

@Composable
fun CategoryFilterDropdownMenu(
    expanded: Boolean,
    categories: List<CategoryEntity>,
    selectedCategoryIds: Set<Int>,
    onCategorySelected: (Int, Boolean) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (categories.isEmpty()) {
            DropdownMenuItem(
                text = { Text("No categories available") },
                onClick = { },
                enabled = false
            )
        } else {
            categories.forEach { category ->
                val isSelected = category.id in selectedCategoryIds
                val itemModifier = if (isSelected) {
                    Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                } else {
                    Modifier
                }
                DropdownMenuItem(
                    modifier = itemModifier,
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = category.name)
                        }
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(color = Color(category.colorHex.toULong()), shape = CircleShape)
                        )
                    },
                    onClick = {
                        onCategorySelected(category.id, !isSelected)
                    },
                )
            }
        }
    }
}
