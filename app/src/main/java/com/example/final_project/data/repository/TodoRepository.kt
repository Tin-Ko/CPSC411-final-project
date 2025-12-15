package com.example.final_project.data.repository
import com.example.final_project.data.local.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getTasks(userId: String): Flow<List<TaskWithCategory>> = todoDao.getTasksForUser(userId)

    fun getTasksWithDueDate(ownerId: String): Flow<List<TaskWithCategory>> = todoDao.getTasksWithDueDate(ownerId)


    fun getCategories(userId: String): Flow<List<CategoryEntity>> = todoDao.getCategoriesForUser(userId)

    suspend fun getTaskById(id: Int) = todoDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity) = todoDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = todoDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = todoDao.deleteTask(task)

    suspend fun insertCategory(category: CategoryEntity): Long = todoDao.insertCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = todoDao.deleteCategory(category)
}