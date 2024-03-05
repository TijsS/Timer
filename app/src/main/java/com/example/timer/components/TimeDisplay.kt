package com.example.timer.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.timer.R
import com.example.timer.feature_timer.presentation.components.ComposableRiveAnimationView
import com.example.timer.feature_timer.toHours
import com.example.timer.feature_timer.toMinutes
import com.example.timer.feature_timer.toSeconds
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimeDisplay(timeRemaining: Int, modifier: Modifier = Modifier) {

    val largeTargetDp = 320.dp.value
    val mediumTargetDp = 230.dp.value
    val smallTargetDp = 130.dp.value
    val reallySmallTargetDp = 0.dp.value

    val animationDurationMedium = 1000

    var showDigitalTime by remember { mutableStateOf(true) }

    val secondClock by remember {
        mutableStateOf(ClockValues(Animatable(largeTargetDp), Animatable(0f), Animatable(0f), Animatable(0f)))
    }
    val minuteClock by remember {
        mutableStateOf(ClockValues(Animatable(reallySmallTargetDp), Animatable(0f), Animatable(0f), Animatable(0f)))
    }
    val hourClock by remember {
        mutableStateOf(ClockValues(Animatable(reallySmallTargetDp), Animatable(0f), Animatable(0f), Animatable(0f)))
    }

    val clocks = listOf(secondClock, minuteClock, hourClock)

    BoxWithConstraints {
        LaunchedEffect(timeRemaining) {
            when {
                timeRemaining.toHours() >= 1 -> {
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

                    launch {
                        val targetSecond = (10 - timeRemaining.toSeconds() % 10) * 50f
                        val targetMinute = (10 - timeRemaining.toMinutes() % 10) * 50f

                        // To jump from final animation 0 to first animation 0
                        if ( clock == secondClock && targetSecond == 500f) {
                            clock.time.animateTo(
                                targetValue = 500f,
                                animationSpec = tween(durationMillis = 700)
                            )
                            clock.time.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 0)
                            )
                        }
                        // To jump from final animation 0 to first animation 0
                        else if ( clock == minuteClock && targetMinute == 500f) {
                            clock.time.animateTo(
                                targetValue = 500f,
                                animationSpec = tween(durationMillis = 700)
                            )
                            clock.time.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 0)
                            )
                        }
                        else {
                            clock.time.animateTo(
                                targetValue = when (clock) {
                                    secondClock -> targetSecond
                                    minuteClock -> targetMinute
                                    hourClock -> (10 - (timeRemaining.toHours() % 10)) * 50f
                                    else -> 0f
                                },
                                animationSpec = tween(durationMillis = 700)
                            )
                        }
                    }

                    launch {
                        val targetTenSecond = (10 - timeRemaining.toSeconds() / 10) * 50f
                        val targetTenMinute = (10 - timeRemaining.toMinutes() / 10) * 50f

                        clock.timeTen.animateTo(
                            targetValue = when (clock) {
                                secondClock -> targetTenSecond
                                minuteClock -> targetTenMinute
                                hourClock -> (10 - (timeRemaining.toHours() / 10)) * 50f
                                else -> 0f
                            },
                            animationSpec = tween(durationMillis = 700)
                        )
                    }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .clickable {
                showDigitalTime = !showDigitalTime
            }
    ) {
        if (hourClock.size.value > 0) {
            Clock(
                clockValues = hourClock,
                showDigitalTime = showDigitalTime,
                modifier = modifier
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {

            if (minuteClock.size.value > 0) {
                Clock(
                    clockValues = minuteClock,
                    showDigitalTime = showDigitalTime,
                    modifier = modifier
                )
            }

            Clock(
                clockValues = secondClock,
                showDigitalTime = showDigitalTime,
                modifier = modifier
            )
        }
    }
}

@Composable
fun Clock(
    clockValues: ClockValues, modifier: Modifier = Modifier, showDigitalTime: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var rightTimerAnimation: RiveAnimationView? = null
    var leftTimerAnimation: RiveAnimationView? = null

    Box(
        contentAlignment = Alignment.Center
    ) {

        if (showDigitalTime && clockValues.size.targetValue > 0) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .size(clockValues.size.value.dp.times(0.3f))
            ) {
                ComposableRiveAnimationView(
                    animation = R.raw.timer5,
                    modifier = modifier
                        .weight(1f)
                ) { view ->
                    leftTimerAnimation = view
                }
                ComposableRiveAnimationView(
                    animation = R.raw.timer5,
                    modifier = modifier
                        .weight(1f)
                ) { view ->
                    rightTimerAnimation = view
                }
            }

            LaunchedEffect(clockValues.time.value) {
                leftTimerAnimation?.setNumberState("StateMachine", "time", clockValues.timeTen.value)
                rightTimerAnimation?.setNumberState("StateMachine", "time", clockValues.time.value)
            }
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
    var timeTen: Animatable<Float, AnimationVector1D>,
    val rotation: Animatable<Float, AnimationVector1D>
)

@SuppressLint("UnrememberedAnimatable", "UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    TimerTheme {
        Clock(
            ClockValues(Animatable(320f), Animatable(0f), Animatable(0f), Animatable(0f)),
            showDigitalTime = true
        )
    }
}

@SuppressLint("UnrememberedAnimatable", "UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun Clock1minPreview() {
    TimerTheme {
        Clock(
            ClockValues(Animatable(320f), Animatable(450f), Animatable(450f), Animatable(450f)),
            showDigitalTime = true
        )
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