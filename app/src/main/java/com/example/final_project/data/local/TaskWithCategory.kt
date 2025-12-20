package com.example.final_project.data.local
import androidx.room.Embedded
import androidx.room.Relation

// This class is for when we get a task for the task list, it comes with both the task and the category.
data class TaskWithCategory(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)
