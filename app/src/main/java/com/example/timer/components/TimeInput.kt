package com.example.timer.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    addSeconds: ( Long ) -> Unit,
    resetInput: Boolean,
    small: Boolean = false
) {
    val (infiniteCircularListWidth, infiniteCircularListHeight) = if(small) 25.dp to 20.dp else 50.dp to 40.dp

    var secondInput by remember{ mutableLongStateOf(0)}
    var minuteInput by remember{ mutableLongStateOf(0)}
    var hourInput by remember{ mutableLongStateOf(0)}

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
            initialItem = 0,
            resetInput = resetInput,
            onItemSelected = { hours -> hourInput = hours },
            small = small,
            numberOfDisplayedItems = if ( small ) 1 else 3
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .offset(y = (-4).dp)
        )

        InfiniteCircularList(
            width = infiniteCircularListWidth,
            itemHeight = infiniteCircularListHeight,
            items = (0..59).toMutableList(),
            initialItem = 0,
            resetInput = resetInput,
            small = small,

            onItemSelected = { minutes -> minuteInput = minutes },
            numberOfDisplayedItems = if ( small ) 1 else 3
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .offset(y = (-4).dp)
        )

        InfiniteCircularList(
            width = infiniteCircularListWidth,
            itemHeight = infiniteCircularListHeight,
            items = (0..59).toMutableList(),
            initialItem = 0,
            resetInput = resetInput,
            small = small,
            onItemSelected = { seconds -> secondInput = seconds },
            numberOfDisplayedItems = if ( small ) 1 else 3
        )

        IconButton(
            onClick = {
                addSeconds(hourInput * 3600 + minuteInput * 60 + secondInput)
                      },
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
                resetInput = false
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
                small = true
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
                resetInput = false
            )
        }
    }
}