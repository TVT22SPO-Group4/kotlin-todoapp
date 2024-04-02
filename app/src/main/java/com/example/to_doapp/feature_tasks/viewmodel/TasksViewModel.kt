package com.example.to_doapp.feature_tasks.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.model.Task
import com.example.to_doapp.data.repositories.TaskRepository
import com.example.to_doapp.feature_tasks.events.TasksScreenUiEvent
import com.example.to_doapp.feature_tasks.side_effects.TaskScreenSideEffects
import com.example.to_doapp.feature_tasks.state.TasksScreenUiState
import com.example.to_doapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val _state: MutableStateFlow<TasksScreenUiState> = MutableStateFlow(TasksScreenUiState())
    val state: StateFlow<TasksScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<TaskScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(TasksScreenUiEvent.GetTasks)
    }

    private fun updateState(newState: TasksScreenUiState) {
        _state.value = newState
    }

    fun sendEvent(event: TasksScreenUiEvent) {
        handleEvent(state = _state.value, event = event)
    }

    private fun handleEffect(builder: () -> TaskScreenSideEffects) {
        val effect = builder()
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun handleEvent(state: TasksScreenUiState, event: TasksScreenUiEvent) {
        when(event) {
            is TasksScreenUiEvent.GetTasks -> {
                getTasks(state = state)
            }
            is TasksScreenUiEvent.AddTask -> {
                addTask(
                    name = event.name,
                    description = event.description,
                    state = state)
            }
            is TasksScreenUiEvent.DeleteTask -> {
                deleteTask(taskId = event.taskId, state = state)
            }
            is TasksScreenUiEvent.OnChangeTaskDescription -> {
                onChangeTaskDescription(state = state, description = event.description)
            }
            is TasksScreenUiEvent.OnChangeTaskName -> {
                onChangeTaskName(state = state, name = event.name)
            }
            is TasksScreenUiEvent.SetTaskToBeUpdated -> {
                setTaskToBeUpdated(state = state, task = event.taskToBeUpdated)
            }
            is TasksScreenUiEvent.OnChangeAddTaskDialogState -> {
                onChangeAddTaskDialog(state = state, isShow = event.show)
            }
            is TasksScreenUiEvent.OnChangeUpdateTaskDialogState -> {
                onChangeEditTaskDialog(state = state, isShow = event.show)
            }
            is TasksScreenUiEvent.UpdateTask -> {
                updateTask(state = state)
            }
        }
    }

    private fun updateTask(state: TasksScreenUiState) {
        viewModelScope.launch {
            updateState(state.copy(
                isLoading = true
            ))
            val name = state.currentTextFieldName
            val description = state.currentTextFieldDescription
            val taskToUpdate = state.taskToUpdate

            when (val result = taskRepository.editTask(
                name = name,
                description = description,
                taskId = taskToUpdate?.id ?: ""
            )) {
                is Result.Success -> {
                    updateState(state.copy(
                        isLoading = false,
                        currentTextFieldName = "",
                        currentTextFieldDescription = ""
                    ))

                    sendEvent(TasksScreenUiEvent.OnChangeUpdateTaskDialogState(show = false))

                    sendEvent(TasksScreenUiEvent.GetTasks)

                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = "Task updated"
                    ) }
                }

                is Result.Failure -> {
                    updateState(state.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "An error occurred"

                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = errorMessage
                    ) }
                }
            }
        }
    }

    private fun deleteTask(taskId: String, state: TasksScreenUiState) {
        viewModelScope.launch {
            updateState(state.copy(isLoading = true))

            when (val result = taskRepository.deleteTask(taskId = taskId)) {
                is Result.Success -> {
                    updateState(state.copy(
                        isLoading = false
                    ))

                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = "Task deleted"
                    ) }

                    sendEvent(TasksScreenUiEvent.GetTasks)
                }
                is Result.Failure -> {
                    updateState(state.copy(
                        isLoading = false
                    ))

                    val errorMessage = result.exception.message ?: "An error occurred"
                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = errorMessage
                    ) }
                }
            }
        }
    }

    private fun addTask(name: String, description: String, state: TasksScreenUiState) {
        viewModelScope.launch {
            updateState(state.copy(isLoading = true))

            when (val result = taskRepository.addTask(name = name, description = description)) {
                is Result.Success -> {
                    updateState(state.copy(
                        isLoading = false,
                        currentTextFieldName = "",
                        currentTextFieldDescription = ""
                    ))

                    sendEvent(TasksScreenUiEvent.OnChangeAddTaskDialogState(show = false))

                    sendEvent(TasksScreenUiEvent.GetTasks)

                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = "Task added"
                    ) }
                }

                is Result.Failure -> {
                    updateState(state.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "An error occurred"
                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = errorMessage
                    ) }
                }
            }
        }
    }

    private fun getTasks(state: TasksScreenUiState) {
        viewModelScope.launch {
            updateState(state.copy(isLoading = true))

            when (val result = taskRepository.getAllTasks()) {
                is Result.Success -> {
                    updateState(state.copy(isLoading = false, tasks = result.data))
                }

                is Result.Failure -> {
                    updateState(
                        state.copy(isLoading = false)
                    )
                    val errorMessage = result.exception.message ?: "An error occurred"
                    handleEffect { TaskScreenSideEffects.ShowSnackBarMessage(
                        message = errorMessage
                    ) }
                }
            }
        }
    }

    private fun setTaskToBeUpdated(state: TasksScreenUiState, task: Task) {
        updateState(state.copy(taskToUpdate = task))
    }

    private fun onChangeTaskName(state: TasksScreenUiState, name: String) {
        updateState(state.copy(currentTextFieldName = name))
    }

    private fun onChangeTaskDescription(state: TasksScreenUiState, description: String) {
        updateState(state.copy(currentTextFieldDescription = description))
    }

    private fun onChangeEditTaskDialog(state: TasksScreenUiState, isShow: Boolean) {
        updateState(state.copy(isShowEditTaskDialogState = isShow))
    }

    private fun onChangeAddTaskDialog(state: TasksScreenUiState, isShow: Boolean) {
        updateState(state.copy(isShowAddTaskDialogState = isShow))
    }
}