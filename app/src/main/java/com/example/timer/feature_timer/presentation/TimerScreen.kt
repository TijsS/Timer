package com.example.timer.feature_timer.presentation

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.example.timer.R
import com.example.timer.components.InfiniteCircularList
import com.example.timer.components.KeepScreenOn
import com.example.timer.components.TimeDisplay
import com.example.timer.components.TimeInput
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerService
import com.example.timer.feature_timer.TimerState
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.flow.collectLatest
import org.intellij.lang.annotations.JdkConstants.FontStyle

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("RememberReturnType", "SuspiciousIndentation")
@Composable
fun TimerScreen(
    startListening: () -> Unit,
    context: Context = LocalContext.current,
    timerViewModel: TimerViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {
    val timerUiState by timerViewModel.uiState.collectAsState()
    var alarmAnimation: RiveAnimationView? = null
    val applicationContext = context.applicationContext

    val timeRemaining by remember { ClockTimer.timeRemaining }
    val timerState by remember { ClockTimer.timerState }
    val currentDistanceAnimated by animateFloatAsState(
        targetValue = timerUiState.dismissPercentage,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        finishedListener = { value ->
            if (value >= 100f) {
                timerViewModel.stopCountDown()
            }
        }, label = ""
    )


    LaunchedEffect(key1 = true) {
        timerViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is TimerViewModel.UiEvent.StopTimer -> {
                    Intent(applicationContext, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Stop.toString()
                        applicationContext.startService(intent)
                    }
                }

                is TimerViewModel.UiEvent.ResetTimer -> {
                    Intent(applicationContext, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Reset.toString()
                        applicationContext.startService(intent)
                    }
                }

                is TimerViewModel.UiEvent.PauseTimer -> {
                    Intent(applicationContext, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Pause.toString()
                        applicationContext.startService(intent)
                    }
                }

                is TimerViewModel.UiEvent.StartTimer -> {
                    Intent(applicationContext, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Start.toString()
                        applicationContext.startService(intent)

                        val bindIntent = Intent(context, TimerService::class.java)
                        val serviceConnection = object : ServiceConnection {
                            override fun onServiceConnected(
                                name: ComponentName?,
                                service: IBinder?
                            ) {
                            }

                            override fun onServiceDisconnected(name: ComponentName?) {
                            }
                        }

                        context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(currentDistanceAnimated) {
        alarmAnimation?.setNumberState("StateMachine", "dismissSwipe", currentDistanceAnimated)
    }

    when (timerState) {
        TimerState.Finished, TimerState.Running -> KeepScreenOn()
        else -> {}
    }

    if (timerState == TimerState.Finished) {
        Box(
            contentAlignment = Alignment.Center,
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
            BoxWithConstraints {
                ComposableRiveAnimationView(
                    animation = R.raw.alarm,
                    stateMachineName = "StateMachine",
                    fit = Fit.COVER,
                    modifier = Modifier
                        .size(maxWidth * 0.7f,  maxHeight* 0.7f)
                ) { view ->
                    alarmAnimation = view
                    alarmAnimation?.fireState("StateMachine", "startRinging")
                }
            }
        }
    }
    else {
        if (windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                TimeDisplay(
                    timeRemaining = timeRemaining,
                    modifier = Modifier
                        .weight(1f)
                )

                TimeControlArea(
                    timerState = { timerState },
                    resetInput = timerUiState.resetInput,
                    startCountDown = { timerViewModel.startCountDown() },
                    pauseCountdown = { timerViewModel.pauseCountDown() },
                    resetCountDown = { timerViewModel.stopCountDown() },
                    startListening = { startListening() },
                    addTime = { timerViewModel.addSecondsToTimer() },
                    secondInput = { timerViewModel.setSecondInput(it) },
                    minuteInput = { timerViewModel.setMinuteInput(it) },
                    hourInput = { timerViewModel.setHourInput(it) },
                    timerGreaterThenZero = { timeRemaining > 0 },
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
        else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                TimeDisplay(
                    timeRemaining = timeRemaining,
                    modifier = Modifier
                        .weight(2f)
                )

                TimeControlArea(
                    timerState = { timerState },
                    resetInput = timerUiState.resetInput,
                    startCountDown = { timerViewModel.startCountDown() },
                    pauseCountdown = { timerViewModel.pauseCountDown() },
                    resetCountDown = { timerViewModel.stopCountDown() },
                    startListening = { startListening() },
                    addTime = { timerViewModel.addSecondsToTimer() },
                    secondInput = { timerViewModel.setSecondInput(it) },
                    minuteInput = { timerViewModel.setMinuteInput(it) },
                    hourInput = { timerViewModel.setHourInput(it) },
                    timerGreaterThenZero = { timeRemaining > 0 },
                    modifier = Modifier
                        .weight(1.5f)
                )
            }
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
                    autoplay = true,
                    alignment = alignment,
                    fit = fit,
                    )
            }
        },
        update = { view -> onInit(view) }
    )
}

@Composable
fun TimeControlArea(
    timerState: () -> TimerState,
    startCountDown: () -> Unit,
    pauseCountdown: () -> Unit,
    resetCountDown: () -> Unit,
    startListening: () -> Unit,
    addTime: () -> Unit,
    secondInput: (Int) -> Unit,
    minuteInput: (Int) -> Unit,
    hourInput: (Int) -> Unit,
    timerGreaterThenZero: () -> Boolean,
    resetInput: Boolean,
    modifier: Modifier = Modifier,
) {
    Column (
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 8.dp)
    ) {

        TimeInput(
            hourInput = hourInput,
            minuteInput = minuteInput,
            secondInput = secondInput,
            addTime = addTime,
            resetInput = resetInput
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { resetCountDown() },
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "reset"
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            TimerActionButton(
                timerState = timerState,
                timerGreaterThenZero = timerGreaterThenZero,
                startCountDown = startCountDown,
                pauseCountDown = pauseCountdown,
                modifier = Modifier
                    .weight(1f)
            )
        }

        IconButton(
            onClick = { startListening() },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_mic_24),
                contentDescription = "Audio input"
            )
        }
    }
}

@Composable
fun TimerActionButton(
    timerState: () -> TimerState,
    timerGreaterThenZero: () -> Boolean,
    startCountDown: () -> Unit,
    pauseCountDown: () -> Unit,
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
                pauseTimer = { pauseCountDown() },
                modifier = modifier
            )

            else -> {}
        }
    }
}

@Composable
fun PauseTimerButton(
    pauseTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { pauseTimer() },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_pause_24),
            contentDescription = "Start timer"
        )
    }
}

@Composable
fun StartTimerButton(
    modifier: Modifier = Modifier,
    timerGreaterThenZero: () -> Boolean,
    startTimer: () -> Unit,
) {
    IconButton(
        onClick = { startTimer() },
        enabled = timerGreaterThenZero(),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_play_arrow_24),
            contentDescription = "Start timer"
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, widthDp = 412, heightDp = 892)
@Composable
fun TimerScreenVertical() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TimerScreen(
                startListening = { },
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    DpSize.Zero.copy(
                        412.dp,
                        892.dp
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true, device = Devices.AUTOMOTIVE_1024p, widthDp = 892, heightDp = 412)
@Composable
fun TimerScreenHorizontal() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TimerScreen(
                startListening = { },
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    DpSize.Zero.copy(
                        892.dp,
                        412.dp
                    )
                ),
            )
        }
    }
}