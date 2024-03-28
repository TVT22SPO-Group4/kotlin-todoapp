package com.example.to_doapp.data.repositories

import com.example.to_doapp.data.model.Task
import com.example.to_doapp.util.Result

interface TaskRepository {
    suspend fun addTask(name: String, description: String): Result<Unit>

    suspend fun getAllTasks(): Result<List<Task>>

    suspend fun editTask(taskId: String, name: String, description: String): Result<Unit>

    suspend fun deleteTask(taskId: String): Result<Unit>
}