package com.example.to_doapp.feature_tasks.state

import com.example.to_doapp.data.model.Task
import com.example.to_doapp.feature_tasks.events.TasksScreenUiEvent

data class TasksScreenUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val taskToUpdate: Task? = null,
    val currentTextFieldName: String = "",
    val currentTextFieldDescription: String = "",
    val isShowAddTaskDialogState: Boolean = false,
    val isShowEditTaskDialogState: Boolean = false
)