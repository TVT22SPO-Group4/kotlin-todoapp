package com.example.to_doapp.feature_tasks.side_effects

sealed class TaskScreenSideEffects {
    data class ShowSnackBarMessage(val message: String): TaskScreenSideEffects()
}