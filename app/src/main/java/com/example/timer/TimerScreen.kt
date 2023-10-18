package com.example.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timer.components.TimeDisplay

@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val timerUiState by timerViewModel.uiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TimeDisplay(
            timeRemaining = timerUiState.timeRemaining,
            modifier = Modifier
                .weight(2f)
        )

//        CurrentTime(
//            time = timerUiState.timeRemaining
//        )

        TimeControlArea(
            addSecondsToTimer = { timerViewModel.addSecondsToTimer(it) },
            timerState = timerUiState.timerState,
            startCountDown = { timerViewModel.startCountDown() },
            cancelCountDown = { timerViewModel.cancelCountDown() },
            resetCountDown = { timerViewModel.resetCountDown() },
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
fun CurrentTime(time: Int, modifier: Modifier = Modifier) {
    Text(text = time.toString())
}

@Composable
fun TimeControlArea(
    addSecondsToTimer: (Int) -> Unit,
    timerState: TimerState,
    startCountDown: () -> Unit,
    cancelCountDown: () -> Unit,
    resetCountDown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
            .fillMaxWidth()
        ) {
            AddTimeButton(addSecondsToTimer = { addSecondsToTimer(1) }, time = 1)
            AddTimeButton(addSecondsToTimer = { addSecondsToTimer(10) }, time = 10)
            AddTimeButton(addSecondsToTimer = { addSecondsToTimer(3600) }, time = 3600)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
                .fillMaxWidth()
        ) {
            timerState.let {
                when (it) {
                    TimerState.Paused -> StartTimerButton(startTimer = { startCountDown() })
                    TimerState.Running -> PauseTimerButton(pauseTimer = { cancelCountDown() })
                    TimerState.Stopped -> StartTimerButton(startTimer = { startCountDown() })
                }
            }

            Button(
                onClick = { resetCountDown() },
                modifier = modifier
            ) {
                Text(text = stringResource(R.string.reset_timer))
            }
        }
    }
}

@Composable
fun AddTimeButton(
    addSecondsToTimer: (Int) -> Unit,
    time: Int,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = { addSecondsToTimer(time) },
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.add_seconds, time))
    }
}

@Composable
fun PauseTimerButton(
    pauseTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = { pauseTimer() },
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.pause_timer))
    }
}

@Composable
fun StartTimerButton(
    modifier: Modifier = Modifier,
    startTimer: () -> Unit
) {
    Button(
        onClick = { startTimer() },
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.start_timer))
    }
}