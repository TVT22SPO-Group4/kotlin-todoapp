package com.example.to_doapp.feature_tasks.events

import com.example.to_doapp.data.model.Task

sealed class TasksScreenUiEvent {
    object GetTasks : TasksScreenUiEvent()

    data class AddTask(val name: String, val description: String) : TasksScreenUiEvent()

    object UpdateTask : TasksScreenUiEvent()

    data class DeleteTask(val taskId: String) : TasksScreenUiEvent()

    data class OnChangeTaskName(val name: String) : TasksScreenUiEvent()

    data class OnChangeTaskDescription(val description: String) : TasksScreenUiEvent()

    data class OnChangeAddTaskDialogState(val show: Boolean) : TasksScreenUiEvent()

    data class OnChangeUpdateTaskDialogState(val show: Boolean) :
        TasksScreenUiEvent()

    data class SetTaskToBeUpdated(val taskToBeUpdated: Task) : TasksScreenUiEvent()
}