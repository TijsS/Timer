package com.example.timer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun ClockColon( size: TextStyle )
{
    Text(
        text = ":",
        style = size,
        modifier = Modifier
            .padding(horizontal = 12.dp)
    )
}