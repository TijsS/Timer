/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.weartimer.presentation

import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.VIBRATE
import android.Manifest.permission.WAKE_LOCK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import com.example.weartimer.ClockTimer
import com.example.weartimer.R
import com.example.weartimer.TimerService
import com.example.weartimer.TimerState
import com.example.weartimer.WearTimerApp
import com.example.weartimer.presentation.theme.TimerTheme
import com.example.weartimer.timeRemainingToClockFormat
import com.google.android.horologist.composables.TimePicker
import kotlinx.coroutines.launch
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    private val timerApp = WearTimerApp() // Create an instance of TimerApp

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(
        POST_NOTIFICATIONS,
        FOREGROUND_SERVICE,
        VIBRATE,
        WAKE_LOCK
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                0
            )
        } else {
            // All permissions already granted
        }

        setContent {
            WearApp(applicationContext)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WearApp(applicationContext: Context) {
    val timeRemaining by remember { ClockTimer.timeRemaining }
    val coroutineScope = rememberCoroutineScope()
    val timerState by remember { ClockTimer.timerState }

    TimerTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Greeting(greetingName = timeRemaining.timeRemainingToClockFormat())
//        }

        if (timerState == TimerState.Finished) {
            val state = rememberSwipeToDismissBoxState()
            SwipeToDismissBox(
                state = state,
                onDismissed = {
                    ClockTimer.timerState.value = TimerState.Stopped
                    resetTimer(applicationContext)
                },
            ) { isBackground ->
                if (isBackground) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colors.secondaryVariant)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Dismiss alarm", color = MaterialTheme.colors.onPrimary)
                    }
                }
            }
        } else {

            val pagerState = rememberPagerState(pageCount = {
                2
            })

            val pageIndicatorState: PageIndicatorState = remember {
                object : PageIndicatorState {
                    override val pageOffset: Float
                        get() = pagerState.currentPageOffsetFraction
                    override val selectedPage: Int
                        get() = pagerState.currentPage
                    override val pageCount: Int
                        get() = pagerState.pageCount
                }
            }

            HorizontalPager(state = pagerState) { page ->

                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TimerTime(greetingName = timeRemaining.timeRemainingToClockFormat())
                            ActionRow(
                                timerState = { timerState },
                                applicationContext,
                                Modifier.padding(5.dp)
                            )
                        }
                    }

                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background),
                            verticalArrangement = Arrangement.Center
                        ) {

                            TimePicker(
                                onTimeConfirm = {
                                    val seconds = it.toSecondOfDay()

                                    ClockTimer.timeRemaining.value += seconds

                                    if (ClockTimer.timerState.value == TimerState.Running) {
                                        startTimer(applicationContext)
                                    }

                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                                time = LocalTime.MIN
                            )
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background),
                            verticalArrangement = Arrangement.Center
                        ) {
                            TimerTime(greetingName = "Oops")
                        }
                    }
                }
            }
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState,
                modifier = Modifier
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun startTimer(applicationContext: Context) {
    Intent(applicationContext, TimerService::class.java).also { intent ->
        intent.action = TimerService.Action.Start.toString()
        applicationContext.startService(intent)
//
//        val bindIntent = Intent(applicationContext, TimerService::class.java)
//        val serviceConnection = object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            }
//
//            override fun onServiceDisconnected(name: ComponentName?) {
//            }
//        }
//
//        applicationContext.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun resetTimer(applicationContext: Context) {
    Intent(applicationContext, TimerService::class.java).also { intent ->
        intent.action = TimerService.Action.Reset.toString()
        applicationContext.startService(intent)
    }
}
@RequiresApi(Build.VERSION_CODES.S)
fun pauseTimer(applicationContext: Context) {
    Intent(applicationContext, TimerService::class.java).also { intent ->
        intent.action = TimerService.Action.Pause.toString()
        applicationContext.startService(intent)
    }
}

@Composable
fun TimerTime(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.display3,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = greetingName
    )
}
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ActionRow(
    timerState: () -> TimerState,
    applicationContext: Context,
    modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        Button(
            onClick = { resetTimer(applicationContext) },
            modifier = modifier
                .offset( y = (-20).dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_autorenew_24),
                contentDescription = "reset"
            )
        }

        Button(
            onClick = { /*TODO*/ },
            modifier = modifier
                .offset( y = 20.dp)

        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_mic_24),
                contentDescription = "voice input"
            )
        }

        timerState().let {
            when (it) {
                TimerState.Paused, TimerState.Stopped -> {
                    Button(
                        onClick = { startTimer(applicationContext) },
                        modifier = modifier
                            .offset(y = (-15).dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_play_arrow_24),
                            contentDescription = "start"
                        )
                    }
                }

                TimerState.Running -> {
                    Button(
                        onClick = { pauseTimer(applicationContext) },
                        modifier = modifier
                            .offset(y = -20.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_pause_24),
                            contentDescription = "pause"
                        )
                    }
                }
                else -> {}
            }
        }
    }
}