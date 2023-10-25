package com.example.timer

import androidx.annotation.RawRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.example.timer.components.TimeDisplay

@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val timerUiState by timerViewModel.uiState.collectAsState()

    var animation: RiveAnimationView? = null
    var currentDistance by remember { mutableStateOf(0f) }


    fun onPull(pullDelta: Float): Float = when {
        else -> {
            val newOffset = (currentDistance + pullDelta).coerceAtLeast(0f)
            val dragConsumed = newOffset - currentDistance

            currentDistance = newOffset
            animation?.setNumberState("StateMachine", "dismissSwipe", dismissSwipe)
            dragConsumed
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, dragAmount, x, y ->
                    // Calculate the distance traveled on the Y-axis
                    val deltaY = dragAmount.y
                    val distance = deltaY

                    // Map the distance to the value range [0, 100]
                    value += distance

                    // Ensure the value stays within the 0-100 range
                    value = value.coerceIn(0f, 100f)
                }
            }
    ) {

        if (timerUiState.timerState == TimerState.Finished){
            RiveAnimationComposable(
                animation = R.raw.alarm,
                stateMachineName = "StateMachine",
                fit = Fit.COVER,
            ) { view ->
                animation = view
            }
        }
        else {
            TimeDisplay(
                timeRemaining = timerUiState.timeRemaining,
                modifier = Modifier
                    .weight(2f)
            )

            TimeControlArea(
                addSecondsToTimer = { timerViewModel.addSecondsToTimer(it) },
                timerState = { timerUiState.timerState },
                timerGreaterThenZero = { timerUiState.timeRemaining > 0 },
                startCountDown = { timerViewModel.startCountDown() },
                cancelCountDown = { timerViewModel.cancelCountDown() },
                resetCountDown = { timerViewModel.resetCountDown() },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun RiveAnimationComposable(
    @RawRes animation: Int,
    alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
    fit: Fit = Fit.CONTAIN,
    stateMachineName: String,
    onInit: (RiveAnimationView) -> Unit
) {
    AndroidView(
        factory = { context ->
            RiveAnimationView(context).also {
                it.setRiveResource(
                    resId = animation,
                    stateMachineName = stateMachineName,
                    alignment = alignment,
                    fit = fit,
                )
            }
        },
        update = { view -> onInit(view) },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun CurrentTime(time: Int, modifier: Modifier = Modifier) {
    Text(text = time.toString())
}

@Composable
fun TimeControlArea(
    addSecondsToTimer: (Int) -> Unit,
    timerState: () -> TimerState,
    startCountDown: () -> Unit,
    cancelCountDown: () -> Unit,
    resetCountDown: () -> Unit,
    modifier: Modifier = Modifier,
    timerGreaterThenZero: () -> Boolean
) {
    Column (
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .padding(top = 8.dp)
    ) {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            TimerActionButton(
                timerState = timerState,
                timerGreaterThenZero = timerGreaterThenZero,
                startCountDown = startCountDown,
                cancelCountDown = cancelCountDown,
                modifier = Modifier
                    .weight(2f)
            )

            Button(
                onClick = { resetCountDown() },
                modifier = modifier
                    .weight(1f)
            ) {
                Text(text = stringResource(R.string.reset_timer))
            }
        }
    }
}

@Composable
fun TimerActionButton(
    timerState: () -> TimerState,
    timerGreaterThenZero: () -> Boolean,
    startCountDown: () -> Unit,
    cancelCountDown: () -> Unit,
    modifier: Modifier,
){
    timerState().let {
        when (it) {
            TimerState.Paused, TimerState.Stopped -> StartTimerButton(
                startTimer = { startCountDown() },
                timerGreaterThenZero = timerGreaterThenZero,
                modifier = modifier
            )
            TimerState.Running -> PauseTimerButton(
                pauseTimer = { cancelCountDown() },
                modifier = modifier
            )

            else -> {}
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
    timerGreaterThenZero: () -> Boolean,
    startTimer: () -> Unit,
) {
    Button(
        onClick = { startTimer() },
        enabled = timerGreaterThenZero(),
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.start_timer))
    }
}