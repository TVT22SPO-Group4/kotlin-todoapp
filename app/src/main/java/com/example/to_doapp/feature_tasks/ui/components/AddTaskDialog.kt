package com.example.to_doapp.feature_tasks.ui.components

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
import com.example.to_doapp.feature_tasks.state.TasksScreenUiState

@Composable
fun AddTaskDialog(
    setTaskName: (String) -> Unit,
    setTaskDescription: (String) -> Unit,
    saveTask: () -> Unit,
    closeDialog: () -> Unit,
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
                            text = "New Task",
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.currentTextFieldName,
                        onValueChange = {title -> setTaskName(title)},

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
                                saveTask()
                                setTaskName("")
                                setTaskDescription("")
                                closeDialog()
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}