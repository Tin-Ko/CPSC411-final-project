package com.example.final_project.data.local
import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithCategory(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)
