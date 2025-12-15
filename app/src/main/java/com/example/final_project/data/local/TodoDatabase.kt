package com.example.final_project.data.local
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase()  {
    abstract fun todoDao(): TodoDao
}