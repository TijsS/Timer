package com.example.timer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    hourInput: (Int) -> Unit,
    minuteInput: (Int) -> Unit,
    secondInput: (Int) -> Unit,
    addTime: () -> Unit,
    resetInput: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()

    ) {
        Spacer(modifier = Modifier.width(40.dp))

        InfiniteCircularList(
            width = 50.dp,
            itemHeight = 40.dp,
            items = (0..10).toMutableList(),
            initialItem = 0,
            textStyle = TextStyle(fontSize = 18.sp),
            textColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            resetInput = resetInput,
            onItemSelected = { _, item ->
                hourInput(item)
            }
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .offset(y = (-4).dp)
        )

        InfiniteCircularList(
            width = 50.dp,
            itemHeight = 40.dp,
            items = (0..59).toMutableList(),
            initialItem = 0,
            textStyle = TextStyle(fontSize = 18.sp),
            textColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            onItemSelected = { _, item ->
                minuteInput(item)
            },
            resetInput = resetInput
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .offset(y = (-4).dp)
        )

        InfiniteCircularList(
            width = 50.dp,
            itemHeight = 40.dp,
            items = (0..59).toMutableList(),
            initialItem = 0,
            textStyle = TextStyle(fontSize = 18.sp),
            textColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            onItemSelected = { _, item ->
                secondInput(item)
            },
            resetInput = resetInput
        )

        IconButton(
            onClick = { addTime() },
            modifier =
            Modifier
                .padding(start = 16.dp)
                .width(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add selected time"
            )
        }
    }
}