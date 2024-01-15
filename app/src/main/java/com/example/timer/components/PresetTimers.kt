package com.example.timer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetTimers() {
    val scrollState = rememberLazyListState(0)

    LazyColumn(
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        ),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(3) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                HiddenInput(value = "value${it}") {}
                TimeInput(
                    hourInput = {},
                    minuteInput = {},
                    secondInput = {},
                    addTime = { /*TODO*/ },
                    resetInput = false,
                    small = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenInput(value: String, onValueChange: () -> Unit) {
    TextField(
        value = value,
        onValueChange = { onValueChange() },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .width(150.dp)
            .padding(0.dp)
    )

}

@Composable
@Preview(showBackground = true)
fun PresetTimersPreview() {
    TimerTheme {
        PresetTimers()
    }
}