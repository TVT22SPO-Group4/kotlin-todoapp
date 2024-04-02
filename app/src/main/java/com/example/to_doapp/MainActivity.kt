package com.example.to_doapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_doapp.feature_tasks.events.TasksScreenUiEvent
import com.example.to_doapp.feature_tasks.side_effects.TaskScreenSideEffects
import com.example.to_doapp.feature_tasks.ui.components.AddTaskDialog
import com.example.to_doapp.feature_tasks.ui.components.EmptyComponent
import com.example.to_doapp.feature_tasks.ui.components.LoadingComponent
import com.example.to_doapp.feature_tasks.ui.components.TaskItem
import com.example.to_doapp.feature_tasks.ui.components.UpdateTaskDialog
import com.example.to_doapp.feature_tasks.viewmodel.TasksViewModel
import com.example.to_doapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val tasksViewModel: TasksViewModel = viewModel()

            val state = tasksViewModel.state.collectAsState().value

            val effectFlow = tasksViewModel.effect

            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(key1 = "side_effect") {
                effectFlow.onEach { effect ->
                    when (effect) {
                        is TaskScreenSideEffects.ShowSnackBarMessage -> {
                            snackBarHostState.showSnackbar(
                                message = effect.message,
                                duration = SnackbarDuration.Short,
                                actionLabel = "DISMISS",
                            )
                        }
                    }
                }.collect()
            }

            ToDoAppTheme {
                if(state.isShowAddTaskDialogState) {
                    AddTaskDialog(
                        setTaskName = { name ->
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeTaskName(name = name)
                            )
                        },
                        setTaskDescription = { description ->
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeTaskDescription(description = description)
                            )
                        },
                        saveTask = {
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.AddTask(
                                    name = state.currentTextFieldName,
                                    description = state.currentTextFieldDescription
                                )
                            )
                        },
                        closeDialog = {
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeAddTaskDialogState(show = false)
                            )
                        },
                        state = state
                    )
                }

                if(state.isShowEditTaskDialogState) {
                    UpdateTaskDialog(
                        setTaskName = { name ->
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeTaskName(name = name)
                            )
                        },
                        setTaskDescription = { description ->
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeTaskDescription(description = description)
                            )
                        },
                        updateTask = {
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.UpdateTask
                            )
                        },
                        closeDialog = {
                            tasksViewModel.sendEvent(
                                event = TasksScreenUiEvent.OnChangeUpdateTaskDialogState(show = false)
                            )
                        },
                        task = state.taskToUpdate,
                        state = state
                    )
                }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    },
                    floatingActionButton = {
                        Column {
                            ExtendedFloatingActionButton(
                                icon = {
                                    Icon(
                                        Icons.Rounded.Add,
                                        contentDescription = "Add Task",
                                        tint = Color.Black,
                                    )
                                },

                                text = {
                                    Text(
                                        text = "Add Task",
                                        color = Color.Black,
                                    )
                                },

                                onClick = {
                                    tasksViewModel.sendEvent(
                                        event = TasksScreenUiEvent.OnChangeAddTaskDialogState(show = true),
                                    )
                                },

                                modifier = Modifier.padding(horizontal = 12.dp),
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                            )
                        }
                    },
                    containerColor = Color(0XFFFAFAFA),
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        when {
                            state.isLoading -> {
                                LoadingComponent()
                            }

                            !state.isLoading && state.tasks.isNotEmpty() -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                ) {
                                    item {
                                        Text(
                                            modifier = Modifier.padding(16.dp),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            text = "Your Tasks",
                                        )
                                    }

                                    items(state.tasks) { task ->
                                        TaskItem(
                                            task = task,
                                            deleteTask = { taskId ->
                                                tasksViewModel.sendEvent(
                                                    event = TasksScreenUiEvent.DeleteTask(taskId = taskId)
                                                )
                                            },

                                            editTask = {taskToBeUpdated ->
                                                tasksViewModel.sendEvent(
                                                    event = TasksScreenUiEvent.OnChangeUpdateTaskDialogState(
                                                        show = true
                                                    )
                                                )

                                                tasksViewModel.sendEvent(
                                                    event = TasksScreenUiEvent.SetTaskToBeUpdated(
                                                        taskToBeUpdated = taskToBeUpdated
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            !state.isLoading && state.tasks.isEmpty() -> {
                                EmptyComponent()
                            }

                        }
                    }
                }
            }
        }
    }
}




