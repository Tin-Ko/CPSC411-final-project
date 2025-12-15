package com.example.final_project.data.local
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // For Categories
    @Query("SELECT * FROM categories WHERE ownerId = :userId")
    fun getCategoriesForUser(userId: String): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // For Tasks
    @Transaction
    @Query("SELECT * FROM tasks WHERE ownerId = :userId ORDER BY isCompleted ASC, dueDate IS NULL ASC, dueDate ASC")
    fun getTasksForUser(userId: String): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE ownerId = :ownerId AND dueDate IS NOT NULL")
    fun getTasksWithDueDate(ownerId: String): Flow<List<TaskWithCategory>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskWithCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}