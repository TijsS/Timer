package com.example.timer.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme
import com.example.timer.ui.theme.Values.LARGE_PADDING
import com.example.timer.ui.theme.Values.MEDIUM_PADDING

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    addSeconds: (Long) -> Unit,
    resetInput: Boolean,
    small: Boolean = false,
    savePresetTimer: (Long) -> Unit = { _ -> },
    duration: Long = 0
) {
    val (infiniteCircularListWidth, infiniteCircularListHeight) = if (small) 25.dp to 20.dp else 50.dp to 40.dp

    var secondInput by remember { mutableLongStateOf(duration % 60) }
    var minuteInput by remember { mutableLongStateOf(duration / 60 % 60) }
    var hourInput by remember { mutableLongStateOf(duration / 3600 % 24) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()

    ) {
        Spacer(modifier = Modifier.width(40.dp))

        InfiniteCircularList(
            width = infiniteCircularListWidth,
            itemHeight = infiniteCircularListHeight,
            items = (0..10).toMutableList(),
            initialItem = hourInput.toInt(),
            resetInput = resetInput,
            onItemSelected = { hours ->
                hourInput = hours
                savePresetTimer(hourInput * 3600 + minuteInput * 60 + secondInput)
            },
            small = small,
            numberOfDisplayedItems = if (small) 1 else 3
        )

        ClockColon(size = if (small) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.displaySmall)

        InfiniteCircularList(
            width = infiniteCircularListWidth,
            itemHeight = infiniteCircularListHeight,
            items = (0..59).toMutableList(),
            initialItem = minuteInput.toInt(),
            resetInput = resetInput,
            small = small,
            onItemSelected = { minutes ->
                minuteInput = minutes
                savePresetTimer(hourInput * 3600 + minuteInput * 60 + secondInput)
            },
            numberOfDisplayedItems = if (small) 1 else 3
        )

        ClockColon(size = if (small) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.displaySmall)

        InfiniteCircularList(
            width = infiniteCircularListWidth,
            itemHeight = infiniteCircularListHeight,
            numberOfDisplayedItems = if (small) 1 else 3,
            items = (0..59).toMutableList(),
            initialItem = secondInput.toInt(),
            resetInput = resetInput,
            small = small,
            onItemSelected = { seconds ->
                secondInput = seconds
                savePresetTimer(hourInput * 3600 + minuteInput * 60 + secondInput)
            },
        )

        IconButton(
            onClick = {
                addSeconds(hourInput * 3600 + minuteInput * 60 + secondInput)
            },
            modifier =
            Modifier
                .padding(start = MEDIUM_PADDING)
                .width(if(small) MEDIUM_PADDING else LARGE_PADDING)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add selected time"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun TimeInputPreview() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TimeInput(
                addSeconds = {},
                resetInput = false,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun SmallTimeInputPreview() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TimeInput(
                addSeconds = {},
                resetInput = false,
                small = true,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p)
@Composable
fun TimeInputPreviewHorizontal() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TimeInput(
                addSeconds = {},
                resetInput = false,
            )
        }
    }
}