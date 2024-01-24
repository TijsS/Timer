package com.example.timer.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timer.feature_timer.Timer
import com.example.timer.ui.theme.TimerTheme
import com.example.timer.ui.theme.Values.LARGE_PADDING
import com.example.timer.ui.theme.Values.SMALL_PADDING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetTimers(
    presetTimers: List<Timer>,
    addEmptyPresetTimer: () -> Unit,
    removePresetTimer: (Int) -> Unit,
    updatePresetTimer: (Timer) -> Unit,
    addTimeToClockTimer: (Long) -> Unit
) {
    val scrollState = rememberLazyListState(0)
    var updateTimerJob by remember { mutableStateOf<Job?>(null) }
    LazyColumn(
        contentPadding = PaddingValues(SMALL_PADDING),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState,
        flingBehavior = rememberSnapFlingBehavior(
            lazyListState = scrollState
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = LARGE_PADDING)
    ) {
        items(
            items = presetTimers,
            key = { timer -> timer.id }
        ){ timer ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            // Left empty on purpose, onLongClick only available in combinedClickable
                        },
                        onLongClick = { removePresetTimer(timer.id) },
                        onLongClickLabel = "delete ${timer.name}"
                    )
                    .fillMaxWidth()

            ) {
                HiddenInput(
                    timer = timer,
                    onValueChange = updatePresetTimer,
                )

                TimeInput(
                    addSeconds = addTimeToClockTimer,
                    resetInput = false,
                    small = true,
                    duration = timer.duration,
                    savePresetTimer = { duration ->
                        updateTimerJob?.cancel()
                        updateTimerJob = CoroutineScope(Dispatchers.IO).launch {
                            delay(100) //save after 100 millis of pause in scrolling
                            updatePresetTimer(timer.apply { this.duration = duration })
                        }
                    }
                )
            }

        }
        item {
            AddPresetTimer(addEmptyPresetTimer)
        }
    }
}

@Composable
private fun AddPresetTimer(addTimer: () -> Unit) {
    IconButton(onClick = { addTimer() }) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add preset timer"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenInput(
    timer: Timer,
    onValueChange: (Timer) -> Unit,
) {

    var nameInput by remember { mutableStateOf(timer.name) }
    var updateTimerJob by remember { mutableStateOf<Job?>(null) }
    val focusManager = LocalFocusManager.current

    TextField(
        value = nameInput,
        onValueChange = {
            nameInput = it
            updateTimerJob?.cancel()
            updateTimerJob = CoroutineScope(Dispatchers.IO).launch {
                delay(500) //save after 500 millis of pause in typing
                onValueChange(timer.apply { this.name = nameInput })
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = Modifier
            .width(150.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun PresetTimersPreview() {
    TimerTheme {
        PresetTimers(
            listOf(Timer(1, "brushing", 120), Timer(2, "walk", 60*30)),
            { },
            { },
            { _ -> },
            { _ -> }
        )
    }
}