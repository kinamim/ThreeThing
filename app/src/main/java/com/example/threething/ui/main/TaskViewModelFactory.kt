package com.example.threething.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.threething.ui.main.TaskViewModelFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class TaskViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
