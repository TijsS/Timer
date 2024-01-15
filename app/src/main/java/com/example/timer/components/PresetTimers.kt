package com.example.timer.components

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timer.feature_timer.Timer
import com.example.timer.ui.theme.TimerTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetTimers(
    timers: List<Timer>,
    addTimer: () -> Unit,
    removeTimer: (Int) -> Unit,
    updateTimer: (Int, String, Long) -> Unit
) {
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
        timers.forEach{ timer ->
            item {
                var nameInput by remember { mutableStateOf(timer.name) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .combinedClickable (
                            onClick = {  },
                            onLongClick = { removeTimer(1) },
                            onLongClickLabel = "stringResource(R.string.open_context_menu)"
                        )

                ) {
                    HiddenInput(
                        name = nameInput,
                        onValueChange = updateTimer,
                        updateName =  { nameInput = it }
                    )

                    TimeInput(
                        hourInput = {},
                        minuteInput = {},
                        secondInput = {},
                        addTime = { },
                        resetInput = false,
                        small = true
                    )
                }
            }
        }
        item {
            AddPresetTimer( addTimer )
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
    name: String,
    onValueChange: (Int, String, Long) -> Unit,
    updateName: (String) -> Unit
) {

    TextField(
        value = name,
        onValueChange = {
            updateName(it)
            onValueChange( name, 0)
            Log.d("TAG", "HiddenInput: $it")
                        },
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
        PresetTimers(
            emptyList(),
            {  },
            {  },
            { _, _, _ ->  }
        )
    }
}