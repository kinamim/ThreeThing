package com.example.threething.ui.DailyFocusScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskScreen() {
    var task1 by remember { mutableStateOf("") }
    var task2 by remember { mutableStateOf("") }
    var task3 by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = task1,
            onValueChange = { task1 = it },
            label = { Text("Task 1") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = task2,
            onValueChange = { task2 = it },
            label = { Text("Task 2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = task3,
            onValueChange = { task3 = it },
            label = { Text("Task 3") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
