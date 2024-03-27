package com.example.to_doapp.feature_tasks.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.to_doapp.data.model.Task
import com.example.to_doapp.feature_tasks.state.TasksScreenUiState

@Composable
fun UpdateTaskDialog(
    setTaskName: (String) -> Unit,
    setTaskDescription: (String) -> Unit,
    updateTask: () -> Unit,
    closeDialog: () -> Unit,
    task: Task?,
    state: TasksScreenUiState,
) {
    Dialog(onDismissRequest = { closeDialog() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Update Task",
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.currentTextFieldName,
                        onValueChange = {title -> setTaskName(title)},
                        placeholder = { Text(task?.name ?: "")},
                        label = {
                            Text("Name")
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.currentTextFieldDescription,
                        onValueChange = {description -> setTaskDescription(description)},
                        placeholder = { Text(task?.description ?: "")},
                        label = {
                            Text("Description")
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(
                            onClick = {
                                updateTask()
                                setTaskName("")
                                setTaskDescription("")
                                closeDialog()
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}