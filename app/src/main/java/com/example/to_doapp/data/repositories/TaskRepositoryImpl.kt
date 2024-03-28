package com.example.to_doapp.data.repositories

import android.util.Log
import com.example.to_doapp.data.model.Task
import com.example.to_doapp.di.IoDispatcher
import com.example.to_doapp.util.Result
import com.example.to_doapp.util.getCurrentTimeAsString
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val todoAppDb: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TaskRepository {
    override suspend fun addTask(name: String, description: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val task = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "createdAt" to getCurrentTimeAsString()
                )

                todoAppDb.collection("tasks")
                    .add(task)
                    .await()

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Failure(
                exception = e
            )
        }
    }

    override suspend fun getAllTasks(): Result<List<Task>> {
        return try {
            withContext(ioDispatcher) {
                val tasks = todoAppDb.collection("tasks")
                    .get()
                    .await()
                    .documents.map { document ->
                        Task(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            createdAt = document.getString("createdAt") ?: ""
                        )
                    }

                Result.Success(tasks.toList() ?: emptyList())
            }
        } catch (e: Exception) {
            Result.Failure(
                exception = e
            )
        }
    }

    override suspend fun editTask(taskId: String, name: String, description: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val updatedFields = hashMapOf<String, Any>()

                if (name.isNotBlank()) {
                    updatedFields["name"] = name
                }

                if (description.isNotBlank()) {
                    updatedFields["description"] = description
                }

                if (updatedFields.isNotEmpty()) {
                    todoAppDb.collection("tasks")
                        .document(taskId)
                        .update(updatedFields)
                        .await()
                }

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Failure(
                exception = e
            )
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                todoAppDb.collection("tasks")
                    .document(taskId)
                    .delete()
                    .await()

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Failure(
                exception = e
            )
        }
    }
}