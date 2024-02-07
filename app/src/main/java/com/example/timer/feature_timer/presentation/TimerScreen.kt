package com.example.timer.feature_timer.presentation

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.timer.R
import com.example.timer.components.HorizontalPagerIndicator
import com.example.timer.components.KeepScreenOn
import com.example.timer.components.PresetTimers
import com.example.timer.components.TimeDisplay
import com.example.timer.components.TimeInput
import com.example.timer.components.VerticalPagerIndicator
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerService
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.presentation.components.PauseTimerButton
import com.example.timer.feature_timer.presentation.components.StartTimerButton
import com.example.timer.feature_timer.presentation.components.TimerFinished
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("RememberReturnType", "SuspiciousIndentation")
@Composable
fun TimerScreen(
    startListening: () -> Unit,
    context: Context = LocalContext.current,
    timerViewModel: TimerViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass
) {
    val timerUiState by timerViewModel.uiState.collectAsState()
    var alarmAnimation: RiveAnimationView? = null
    val applicationContext = context.applicationContext
    val scope = rememberCoroutineScope()

    val timeRemaining by remember { ClockTimer.secondsRemaining }
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



    // Bind to TimerService. For now only used to get the same service instance
    // In case Service gets used more intensively it should stop using startService and start using bind
    lateinit var mService: TimerService
    var mBound: Boolean = false
    val bindIntent = Intent(context, TimerService::class.java)
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    LaunchedEffect(true) {
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
                        applicationContext.startForegroundService(intent)

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
        TimerFinished(
            setDismissPercentage = { delta -> timerViewModel.setDismissPercentage(timerUiState.dismissPercentage + delta / 5) },
            resetDismissPercentage = { timerViewModel.setDismissPercentage(0f) },
            setAlarmAnimation = { alarmAnimation = it }
        )
    } else {

        val pagerState = rememberPagerState(initialPage = 0)

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

                VerticalPager(
                    state = pagerState,
                    beyondBoundsPageCount = 1,
                    reverseLayout = true,
                    pageCount = 2,
                    flingBehavior = flingBehavior(
                        state = pagerState,
                        pagerSnapDistance = PagerSnapDistance.atMost(4)
                    ),
                    modifier = Modifier.weight(1f)
                ) { page ->
                    if (page % 2 == 0) {
                        TimeControlArea(
                            timerState = { timerState },
                            resetInput = timerUiState.resetMainTimeInput,
                            startCountDown = { timerViewModel.startCountDown() },
                            pauseCountdown = { timerViewModel.pauseCountDown() },
                            resetCountDown = { timerViewModel.stopCountDown() },
                            startListening = startListening,
                            addSecondsToTimer = { timerViewModel.addSecondsToTimer(it) },
                            timerGreaterThenZero = { timeRemaining > 0 },
                            modifier = Modifier
                                .weight(1f)
                        )
                    } else {
                        PresetTimers(
                            presetTimers = timerUiState.timers,
                            addEmptyPresetTimer = { scope.launch { timerViewModel.addEmptyPresetTimer() } },
                            removePresetTimer = { scope.launch { timerViewModel.removePresetTimer(it) } },
                            updatePresetTimer = { timer ->
                                scope.launch {
                                    timerViewModel.updatePresetTimer(timer)
                                }
                            },
                            addSecondsToTimer = {
                                timerViewModel.addSecondsToTimer(it)
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            }
                        )
                    }
                }
                VerticalPagerIndicator(pagerState = pagerState)
            }
        } else {
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

                Divider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    modifier = Modifier.padding(26.dp)
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        beyondBoundsPageCount = 1,
                        pageCount = 2,
                        flingBehavior = flingBehavior(
                            state = pagerState,
                            pagerSnapDistance = PagerSnapDistance.atMost(4)
                        ),
                    ) { page ->

                        if (page % 2 == 0) {
                            TimeControlArea(
                                timerState = { timerState },
                                resetInput = timerUiState.resetMainTimeInput,
                                startCountDown = { timerViewModel.startCountDown() },
                                pauseCountdown = { timerViewModel.pauseCountDown() },
                                resetCountDown = { timerViewModel.stopCountDown() },
                                startListening = { startListening() },
                                addSecondsToTimer = { timerViewModel.addSecondsToTimer(it) },
                                timerGreaterThenZero = { timeRemaining > 0 },
                                modifier = Modifier
                            )
                        } else {
                            PresetTimers(
                                presetTimers = timerUiState.timers,
                                addEmptyPresetTimer = { scope.launch { timerViewModel.addEmptyPresetTimer() } },
                                removePresetTimer = { scope.launch { timerViewModel.removePresetTimer(it) } },
                                updatePresetTimer = { timer ->
                                    scope.launch {
                                        timerViewModel.updatePresetTimer(timer)
                                    }
                                },
                                addSecondsToTimer = {
                                    timerViewModel.addSecondsToTimer(it)
                                    scope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                }
                            )
                        }
                    }
                    HorizontalPagerIndicator(pagerState = pagerState)
                }
            }
        }
    }
}

@Composable
fun TimeControlArea(
    timerState: () -> TimerState,
    startCountDown: () -> Unit,
    pauseCountdown: () -> Unit,
    resetCountDown: () -> Unit,
    startListening: () -> Unit,
    addSecondsToTimer: (Int) -> Unit,
    timerGreaterThenZero: () -> Boolean,
    resetInput: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxSize()
    ) {

        TimeInput(
            addSecondsToTimer = addSecondsToTimer,
            resetInput = resetInput,
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
                .padding(bottom = 32.dp)
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
) {
    timerState().let {
        when (it) {
            TimerState.Paused, TimerState.Stopped -> StartTimerButton(
                startTimer =  startCountDown,
                timerGreaterThenZero = timerGreaterThenZero,
                modifier = modifier
            )

            TimerState.Running -> PauseTimerButton(
                pauseTimer = pauseCountDown,
                modifier = modifier
            )

            else -> {}
        }
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