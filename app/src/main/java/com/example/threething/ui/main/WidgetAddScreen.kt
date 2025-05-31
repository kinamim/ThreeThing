package com.example.threething.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
// import androidx.compose.material.Button
// import androidx.compose.material.Text
import com.example.threething.widget.WidgetUtils

@Composable
fun WidgetAddScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button(onClick = {
        //     WidgetUtils.openWidgetPicker(context)
        // }) {
        //     Text("Add My Three Things Widget")
        // }
    }
}
