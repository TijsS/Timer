package com.example.timer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.example.timer.components.TimeDisplay
import kotlinx.coroutines.flow.collectLatest

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("RememberReturnType", "SuspiciousIndentation")
@Composable
fun TimerScreen(
    vibrate: ( ) -> Unit,
    stopVibrate: () -> Unit,
    notify: ( String, String, String ) -> Unit,
    updateNotification: ( String, String, String ) -> Unit,
    dismissNotification: () -> Unit,
    timerViewModel: TimerViewModel = viewModel(),
    context: Context = LocalContext.current,
) {
    val timerUiState by timerViewModel.uiState.collectAsState()

    var animation: RiveAnimationView? = null
    val applicationContext = context.applicationContext

    val currentDistanceAnimated by animateFloatAsState(
        targetValue = timerUiState.dismissPercentage,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        finishedListener = { value ->
            if (value >= 100f) {
                timerViewModel.resetCountDown()

                // Only reset the animation when not visible
                animation?.reset()
                animation?.stop()
            }
        }, label = ""
    )

    LaunchedEffect(key1 = true) {
        timerViewModel.eventFlow.collectLatest { event ->
            when(event) {
                is TimerViewModel.UiEvent.TimerFinished -> {
                    vibrate()
//                    notify( "12", context.getString(R.string.timer_finished), context.getString(R.string.time_up) )
                }
                is TimerViewModel.UiEvent.AlarmStopped -> {
                    stopVibrate()
                    dismissNotification()
                }
                is TimerViewModel.UiEvent.StartTimer -> {
//                    notify( "12", "Timer", timerUiState.timeRemaining.intTimeToString() )
                    Intent(applicationContext, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Start.toString()
                        intent.putExtra("seconds", timerUiState.timeRemaining)
                        applicationContext.startService(intent)
                    }
                }
            }
        }
    }

    LaunchedEffect(timerUiState.timeRemaining){
        if (timerUiState.timerState == TimerState.Running) {
//            notify("12", "Timer", timerUiState.timeRemaining.intTimeToString())
        }
    }

    LaunchedEffect(timerUiState.dismissPercentage > 0) {
        if (timerUiState.dismissPercentage > 0f) {
            animation?.stop()
        }
    }

    LaunchedEffect(currentDistanceAnimated) {
        animation?.setNumberState("StateMachine", "dismissSwipe", currentDistanceAnimated)
    }

    LaunchedEffect(currentDistanceAnimated == 0f ) {
        if (currentDistanceAnimated == 0f) {
            animation?.reset()
            animation?.play()
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()

    ) {

        if (timerUiState.timerState == TimerState.Finished ){
            Box(
                contentAlignment = Alignment.Center ,
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            timerViewModel.setDismissPercentage(timerUiState.dismissPercentage + delta / 5)
                        },
                        onDragStopped = {
                            timerViewModel.setDismissPercentage(0f)
                        }
                    ),

            ) {
                ComposableRiveAnimationView(
                    animation = R.raw.alarm,
                    stateMachineName = "StateMachine",
                    fit = Fit.COVER,
                    modifier = Modifier
                        .size(500.dp)
                ) { view ->
                    animation = view
                }
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
fun ComposableRiveAnimationView(
    modifier: Modifier = Modifier,
    @RawRes animation: Int,
    stateMachineName: String? = null,
    alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
    fit: Fit = Fit.CONTAIN,
    onInit: (RiveAnimationView) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).also {
                it.setRiveResource(
                    resId = animation,
                    stateMachineName = stateMachineName,
                    animationName = "Timeline 1",
                    alignment = alignment,
                    fit = fit,
                )
            }
        },
        update = { view -> onInit(view) }
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