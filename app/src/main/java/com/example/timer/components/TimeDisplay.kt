package com.example.timer.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnrememberedAnimatable")
@Composable
fun TimeDisplay(timeRemaining: Int, modifier: Modifier = Modifier) {

    var secondVisibility by remember { mutableStateOf(true) }
    var minuteVisibility by remember { mutableStateOf(false) }
    var hourVisibility by remember { mutableStateOf(false) }

    val largeTargetDp = 320.dp.value
    val mediumTargetDp = 230.dp.value
    val smallTargetDp = 130.dp.value
    val reallySmallTargetDp = 0.dp.value

    val animationDurationMedium = 1000

    val secondDp by remember { mutableStateOf(Animatable(largeTargetDp)) }
    val minuteDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
    val hourDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }

    val secondRotation by remember { mutableStateOf(Animatable(0f)) }
    val minuteRotation by remember { mutableStateOf(Animatable(0f)) }
    val hourRotation by remember { mutableStateOf(Animatable(0f)) }

    val secondClock = ClockState(secondVisibility, Animatable(largeTargetDp), Animatable(0f))
    val minuteClock = ClockState(minuteVisibility, Animatable(reallySmallTargetDp), Animatable(0f))
    val hourClock = ClockState(hourVisibility, Animatable(reallySmallTargetDp), Animatable(0f))
    val clocks = listOf(secondClock, minuteClock, hourClock)

    BoxWithConstraints {
//        LaunchedEffect(timeRemaining) {
//            launch {
//                secondRotation.animateTo(
//                    targetValue = timeRemaining % 60 * 6f, animationSpec = tween(
//                        durationMillis = animationDurationMedium
//                    )
//                )
//
//                //skip to start position to pretend the arc is a circle
//                if (timeRemaining % 60 * 6f == 0f) {
//                    secondRotation.animateTo(
//                        targetValue = 360f, animationSpec = tween(
//                            durationMillis = 0
//                        )
//                    )
//                }
//            }
//
//            launch {
//                val minutesRemaining = (timeRemaining / 60) % 60 * 6f
//                if (minuteRotation.value == minutesRemaining) return@launch
//                minuteRotation.animateTo(
//                    targetValue = minutesRemaining, animationSpec = tween(
//                        durationMillis = if (timeRemaining == 0) 0 else animationDurationMedium
//                    )
//                )
//
//                //skip to start position to pretend the arc is a circle
//                if (minutesRemaining == 0f) {
//                    minuteRotation.animateTo(
//                        targetValue = 360f, animationSpec = tween(
//                            durationMillis = 0
//                        )
//                    )
//                }
//            }
//
//            launch {
//                hourRotation.animateTo(
//                    targetValue = (timeRemaining / 3600) % 60 * 6f, animationSpec = tween(
//                        durationMillis = if (timeRemaining == 0) 0 else animationDurationMedium
//                    )
//                )
//
//                if ((timeRemaining / 3600) % 60 * 6f == 0f) {
//                    hourRotation.animateTo(
//                        targetValue = 360f, animationSpec = tween(
//                            durationMillis = 0
//                        )
//                    )
//                }
//            }
//
//            when {
//                timeRemaining > 3600 -> {
//                    if (maxHeight / maxWidth < 1.1f) {
//                        launch {
//                            secondDp.animateTo(
//                                smallTargetDp, animationSpec = tween(
//                                    durationMillis = animationDurationMedium,
//                                )
//                            )
//
//                            launch {
//                                minuteDp.animateTo(
//                                    smallTargetDp, animationSpec = tween(
//                                        durationMillis = animationDurationMedium,
//                                    )
//                                )
//                            }
//
//                            launch {
//                                hourDp.animateTo(
//                                    smallTargetDp, animationSpec = tween(
//                                        durationMillis = animationDurationMedium,
//                                    )
//                                )
//                            }
//                        }
//                    } else {
//                        launch {
//                            secondDp.animateTo(
//                                smallTargetDp, animationSpec = tween(
//                                    durationMillis = animationDurationMedium,
//                                )
//                            )
//                        }
//                        launch {
//                            minuteDp.animateTo(
//                                smallTargetDp, animationSpec = tween(
//                                    durationMillis = animationDurationMedium,
//                                )
//                            )
//                        }
//
//                        launch {
//                            hourDp.animateTo(
//                                largeTargetDp, animationSpec = tween(
//                                    durationMillis = animationDurationMedium,
//                                )
//                            )
//                        }
//                    }
//
//                    hourVisibility = true
//                    minuteVisibility = true
//                    secondVisibility = true
//
//                }
//
//                timeRemaining > 60 -> {
//                    launch {
//                        secondDp.animateTo(
//                            smallTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    launch {
//                        minuteDp.animateTo(
//                            mediumTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    launch {
//                        hourDp.animateTo(
//                            reallySmallTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    hourVisibility = false
//                    minuteVisibility = true
//                    secondVisibility = true
//                }
//
//                else -> {
//                    launch {
//                        minuteDp.animateTo(
//                            reallySmallTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    launch {
//                        hourDp.animateTo(
//                            reallySmallTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    launch {
//                        secondDp.animateTo(
//                            largeTargetDp, animationSpec = tween(
//                                durationMillis = animationDurationMedium,
//                            )
//                        )
//                    }
//
//                    hourVisibility = false
//                    minuteVisibility = false
//                    secondVisibility = true
//                }
//            }
//        }
        LaunchedEffect(key1 = timeRemaining ) {
            clocks.forEach { clock ->
                launch {
                    clock.rotation.animateTo(
                        targetValue = when (clock) {
                            secondClock -> timeRemaining % 60 * 6f
                            minuteClock -> (timeRemaining / 60) % 60 * 6f
                            hourClock -> (timeRemaining / 3600) % 60 * 6f
                            else -> 0f
                        },
                        animationSpec = tween(durationMillis = 1000)
                    )

                    if (clock.rotation.value == 0f) {
                        clock.rotation.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(durationMillis = 0)
                        )
                    }
                }

                if (timeRemaining > 3600) {
                    val targetDp = if (maxHeight / maxWidth < 1.1f) smallTargetDp else largeTargetDp
                    launch {
                        clock.size.animateTo(targetDp, animationSpec = tween(durationMillis = 1000))
                    }
                } else if (timeRemaining > 60) {
                    launch {
                        clock.size.animateTo(mediumTargetDp, animationSpec = tween(durationMillis = 1000))
                    }
                } else {
                    launch {
                        clock.size.animateTo(reallySmallTargetDp, animationSpec = tween(durationMillis = 1000))
                    }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (minuteDp.value.dp > 0.dp) {
            Clock(
                rotate = hourRotation.value, clockSize = hourDp.value.dp, modifier = Modifier
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (minuteDp.value.dp > 0.dp) {
                Clock(
                    rotate = minuteRotation.value,
                    clockSize = minuteDp.value.dp,
                    modifier = Modifier
                )
            }

            Clock(
                rotate = secondRotation.value, clockSize = secondDp.value.dp, modifier = Modifier
            )
        }
    }
}

@Composable
fun Clock(
    rotate: Float, clockSize: Dp = 128.dp, modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints {
        Canvas(
            modifier = modifier.requiredSize(clockSize)
        ) {
            val radius = size.width * .45f

            val fiveSecondMarkerOpacity = 0.3f
            val secondMarkerOpacity = 0.8f

            //The degree difference between the each 'minute' line
            val angleDegreeDifference = (360f / 60f)

            (1..60).forEach {
                val angleRadDifference =
                    (((angleDegreeDifference * it) - 90f) * (PI / 180f)).toFloat()
                val lineLength = radius * 0.90f

                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = (lineLength * sin(angleRadDifference) + size.center.y)
                )
                val endOffsetLine = Offset(
                    x = ((radius * 0.97f) - ((radius * .05f) / 2)) * cos(angleRadDifference) + size.center.x,
                    y = ((radius * 0.97f) - ((radius * .05f) / 2)) * sin(angleRadDifference) + size.center.y
                )
                drawLine(
                    color = primaryColor.copy(alpha = if (it % 5 == 0) secondMarkerOpacity else fiveSecondMarkerOpacity),
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = radius * .02f,
                )
            }

            drawArc(
                color = primaryColor.copy(alpha = 0.9f),
                topLeft = Offset(
                    x = size.center.x - radius * .8f, y = size.center.y - radius * .8f
                ),
                startAngle = 270f,
                sweepAngle = if (-(360 - rotate) == -360f) 0f else -(360 - rotate),
                useCenter = false,
                style = Stroke(width = radius * .06f),
                size = Size(
                    width = radius * 1.6f, height = radius * 1.6f
                )
            )

            rotate(rotate) {
                drawCircle(
                    color = primaryColor.copy(alpha = 0.2f),
                    style = Stroke(width = radius * .06f),
                    radius = radius * .8f,
                    center = size.center,
                )

                drawCircle(
                    color = primaryColor, center = Offset(
                        x = size.center.x, y = (radius * .80f) * -1 + size.center.y
                    ), radius = radius * .06f
                )
            }
        }
    }
}

data class ClockState(
    val visibility: Boolean,
    val size: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>
)

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    TimerTheme {
        Clock(rotate = 90f)
    }
}

@Preview(showBackground = true)
@Composable
fun Clock1minPreview() {
    TimerTheme {
        Clock(rotate = 450f)
    }
}

@Preview(showBackground = true)
@Composable
fun TimeDisplaySecondPreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 55)
    }
}

@Preview(showBackground = true)
@Composable
fun TimeDisplaySecondAndMinutePreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 2400)
    }
}

@Preview(showBackground = true)
@Composable
fun TimeDisplaySecondAndMinuteAndHourPreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 15200)
    }
}