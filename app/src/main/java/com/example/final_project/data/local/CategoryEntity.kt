package com.example.final_project.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: Long,
    val ownerId: String
)