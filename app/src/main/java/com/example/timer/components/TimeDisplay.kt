package com.example.timer.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.timer.R
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.presentation.components.ComposableRiveAnimationView
import com.example.timer.feature_timer.toHours
import com.example.timer.feature_timer.toMinutes
import com.example.timer.feature_timer.toSeconds
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("UnrememberedAnimatable")
@Composable
fun TimeDisplay(timeRemaining: Int, modifier: Modifier = Modifier) {

//    var secondVisibility by remember { mutableStateOf(true) }
//    var minuteVisibility by remember { mutableStateOf(false) }
//    var hourVisibility by remember { mutableStateOf(false) }

    val largeTargetDp = 320.dp.value
    val mediumTargetDp = 230.dp.value
    val smallTargetDp = 130.dp.value
    val reallySmallTargetDp = 0.dp.value

    val animationDurationMedium = 1000

//    val secondDp by remember { mutableStateOf(Animatable(largeTargetDp)) }
//    val minuteDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
//    val hourDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
//
//    val secondRotation by remember { mutableStateOf(Animatable(0f)) }
//    val minuteRotation by remember { mutableStateOf(Animatable(0f)) }
//    val hourRotation by remember { mutableStateOf(Animatable(0f)) }

    val secondClock by remember {
        mutableStateOf(ClockValues(Animatable(largeTargetDp), Animatable(0f), Animatable(0f)))
    }
    val minuteClock by remember {
        mutableStateOf(ClockValues(Animatable(reallySmallTargetDp), Animatable(0f), Animatable(0f)))
    }
    val hourClock by remember {
        mutableStateOf(ClockValues(Animatable(reallySmallTargetDp), Animatable(0f), Animatable(0f)))
    }

    val clocks = listOf(secondClock, minuteClock, hourClock)

    BoxWithConstraints {
        LaunchedEffect(timeRemaining) {
            when {
                timeRemaining.toHours() > 1 -> {
                    if (maxHeight / maxWidth < 1.1f) {
                        launch {
                            secondClock.size.animateTo(
                                smallTargetDp, animationSpec = tween(
                                    durationMillis = animationDurationMedium,
                                )
                            )

                            launch {
                                minuteClock.size.animateTo(
                                    smallTargetDp,
                                    animationSpec = tween(durationMillis = animationDurationMedium)
                                )
                            }

                            launch {
                                hourClock.size.animateTo(
                                    smallTargetDp,
                                    animationSpec = tween(durationMillis = animationDurationMedium)
                                )
                            }
                        }
                    } else {
                        launch {
                            secondClock.size.animateTo(
                                smallTargetDp,
                                animationSpec = tween(durationMillis = animationDurationMedium)
                            )
                        }
                        launch {
                            minuteClock.size.animateTo(
                                smallTargetDp,
                                animationSpec = tween(durationMillis = animationDurationMedium)
                            )
                        }

                        launch {
                            hourClock.size.animateTo(
                                largeTargetDp,
                                animationSpec = tween(durationMillis = animationDurationMedium)
                            )
                        }
                    }
                }

                timeRemaining > 60 -> {
                    launch {
                        secondClock.size.animateTo(
                            smallTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }

                    launch {
                        minuteClock.size.animateTo(
                            mediumTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }

                    launch {
                        hourClock.size.animateTo(
                            reallySmallTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }
                }

                else -> {
                    launch {
                        minuteClock.size.animateTo(
                            reallySmallTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }

                    launch {
                        hourClock.size.animateTo(
                            reallySmallTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }

                    launch {
                        secondClock.size.animateTo(
                            largeTargetDp, animationSpec = tween(
                                durationMillis = animationDurationMedium,
                            )
                        )
                    }
                }
            }

            clocks.forEach { clock ->
                launch {

                    // To keep the minute clock from rotating to 0 during 1:00:59 -> 1:00:00
                    if (clock == minuteClock && clock.rotation.value.toInt() == timeRemaining.toMinutes()) return@launch

                    //skip to start position to pretend the arc is a circle
                    if (clock.rotation.targetValue == 0f) {
                        clock.rotation.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(durationMillis = 0)
                        )
                    }
                    launch {
                    clock.rotation.animateTo(
                        targetValue = when (clock) {
                            secondClock -> timeRemaining.toSeconds() * 6f
                            minuteClock -> timeRemaining.toMinutes() * 6f
                            hourClock -> timeRemaining.toHours() * 6f
                            else -> 0f
                        },
                        animationSpec = tween(durationMillis = 900)
                    )
}
                    clock.time.animateTo(
                        targetValue = when (clock) {
                            secondClock -> timeRemaining.toSeconds().toFloat()
                            minuteClock -> timeRemaining.toMinutes().toFloat()
                            hourClock -> timeRemaining.toHours().toFloat()
                            else -> 0f
                        },
                        animationSpec = tween(durationMillis = 700)
                    )
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (hourClock.size.value > 0) {
            Clock(
                clockValues = hourClock,
                modifier = Modifier
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (minuteClock.size.value > 0) {
                Clock(
                    clockValues = minuteClock,
                    modifier = Modifier
                )
            }

            Clock(
                clockValues = secondClock,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun Clock(
    clockValues: ClockValues, modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var alarmAnimation: RiveAnimationView? = null
    var alarmAnimation2: RiveAnimationView? = null

    val animateRight by animateFloatAsState(
        targetValue = ( 10 - ( clockValues.time.value.toFloat() % 10 ) ) * 50,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = ""
    )
    val animateLeft by animateFloatAsState(targetValue = ( 10 - ( clockValues.time.value / 10 ) ) * 50f)


    BoxWithConstraints(
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
            .size(maxWidth.times(0.2f))
        ) {
            ComposableRiveAnimationView(
                animation = R.raw.timer3,
                modifier = Modifier
                    .weight(1f)
            ) {
                    view ->
                alarmAnimation = view
            }
            ComposableRiveAnimationView(
                animation = R.raw.timer3,
                modifier = Modifier
                    .weight(1f)
            ) {
                    view ->
                alarmAnimation2 = view
            }
        }

        LaunchedEffect(clockValues.time.value) {

            alarmAnimation?.setNumberState("StateMachine", "time", animateLeft)
            alarmAnimation2?.setNumberState("StateMachine", "time", animateRight)
        }

        Canvas(
            modifier = modifier.requiredSize(clockValues.size.value.dp)
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
                sweepAngle = if (-(360 - clockValues.rotation.value) == -360f) 0f else -(360 - clockValues.rotation.value),
                useCenter = false,
                style = Stroke(width = radius * .06f),
                size = Size(
                    width = radius * 1.6f, height = radius * 1.6f
                )
            )


            rotate(clockValues.rotation.value) {
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

data class ClockValues(
    val size: Animatable<Float, AnimationVector1D>,
    var time: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>
)

@SuppressLint("UnrememberedAnimatable", "UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    TimerTheme {
        Clock(ClockValues(Animatable(320f), Animatable(0f), Animatable(0f)))
    }
}

@SuppressLint("UnrememberedAnimatable", "UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun Clock1minPreview() {
    TimerTheme {
        Clock(ClockValues(Animatable(320f), Animatable(450f), Animatable(450f)))
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