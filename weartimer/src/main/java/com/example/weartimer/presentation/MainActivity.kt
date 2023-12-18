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
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Text
import com.example.weartimer.ClockTimer
import com.example.weartimer.R
import com.example.weartimer.WearTimerApp
import com.example.weartimer.timeRemainingToClockFormat
import com.example.weartimer.presentation.theme.TimerTheme
import com.google.android.horologist.composables.TimePicker
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
            WearApp()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WearApp() {
    val timeRemaining by remember { ClockTimer.timeRemaining }

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
                        Greeting(greetingName = timeRemaining.timeRemainingToClockFormat())
                        ActionRow(
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
                                Log.d("TAG", "WearApp: WearApp: onTimeConfirm: $it")


                            },
                            time = LocalTime.now()
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
                        Greeting(greetingName = "Oops")
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

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.display3,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = greetingName
    )
}
@Composable
fun ActionRow(modifier: Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = modifier
                .offset( y = (-15).dp)
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

        Button(
            onClick = { /*TODO*/ },
            modifier = modifier
                .offset( y = (-15).dp)

        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_play_arrow_24),
                contentDescription = "start"
            )
        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp( )
}