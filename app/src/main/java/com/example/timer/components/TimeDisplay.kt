package com.example.timer.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("RememberReturnType", "CoroutineCreationDuringComposition")
@Composable
fun TimeDisplay(timeRemaining: Int, modifier: Modifier = Modifier) {

    var secondVisibility by remember { mutableStateOf(true) }
    var minuteVisibility by remember { mutableStateOf(false) }
    var hourVisibility by remember { mutableStateOf(false) }

    val largeTargetDp =  375.dp.value
    val mediumTargetDp = 250.dp.value
    val smallTargetDp = 160.dp.value
    val reallySmallTargetDp = 12.dp.value

    val secondDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
    val minuteDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
    val hourDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }

    when {
        timeRemaining > 3600 -> {
            LaunchedEffect(timeRemaining) {
                launch {
                    secondDp.animateTo(
                        smallTargetDp,
                        animationSpec = tween(
                            durationMillis = 2000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    minuteDp.animateTo(
                        smallTargetDp,
                        animationSpec = tween(
                            durationMillis = 2000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    hourDp.animateTo(
                        largeTargetDp,
                        animationSpec = tween(
                            durationMillis = 2000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }

            hourVisibility = true
            minuteVisibility = true
            secondVisibility = true

        }

        timeRemaining > 60 -> {
            LaunchedEffect(timeRemaining) {
                launch {
                    secondDp.animateTo(
                        smallTargetDp,
                        animationSpec = tween(
                            durationMillis = 2000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    minuteDp.animateTo(
                        mediumTargetDp,
                        animationSpec = tween(
                            durationMillis = 2000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    hourDp.animateTo(
                        reallySmallTargetDp,
                        animationSpec = tween(
                            durationMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                hourVisibility = false
                minuteVisibility = true
                secondVisibility = true
            }
        }
        else -> {
            LaunchedEffect(timeRemaining) {
                launch {
                    minuteDp.animateTo(
                        reallySmallTargetDp,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    hourDp.animateTo(
                        reallySmallTargetDp,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    secondDp.animateTo(
                        largeTargetDp,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }

            hourVisibility = false
            minuteVisibility = false
            secondVisibility = true
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = timeRemaining > 3600,
            enter = slideInVertically(),
            modifier = Modifier
                .weight(1f)
        ) {
            Clock(
                timeRemaining = (timeRemaining / 3600) % 60,
                canvasSize = hourDp.targetValue.dp,
                clockSize = hourDp.value.dp,
                modifier = Modifier
            )
        }
//        Text(text = secondDp.value.dp.toString())
//        Text(text = minuteDp.value.dp.toString())
//        Text(text = hourDp.value.dp.toString())
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AnimatedVisibility(
                visible = timeRemaining > 60,
//                enter = slideInHorizontally(
//                    initialOffsetX = { fullWidth -> -fullWidth },
//                    animationSpec = tween(
//                        durationMillis = 300,
//                        delayMillis = 200,
//                        easing = FastOutSlowInEasing
//                    )
//                ),
                modifier = Modifier
            ) {
                Clock(
                    timeRemaining = (timeRemaining / 60) % 60,
                    canvasSize = minuteDp.targetValue.dp,
                    clockSize = minuteDp.value.dp,
                    modifier = Modifier
                )
            }

            Clock(
                timeRemaining = timeRemaining % 60,
                canvasSize = secondDp.targetValue.dp,
                clockSize = secondDp.value.dp,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun Clock(timeRemaining: Int, canvasSize: Dp = 128.dp, clockSize: Dp = 128.dp, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    BoxWithConstraints {
        Canvas(modifier = modifier
            .requiredSize(clockSize)
//            .background(Color.Green)
        ) {
            val radius = size.width * .45f
            drawCircle(
                color = Color.Black,
                style = Stroke(width = radius * .02f),
                radius = radius,
                center = size.center
            )

            //The degree difference between the each 'minute' line
            val angleDegreeDifference = (360f / 60f)

            (1..60).forEach {
                val angleRadDifference =
                    (((angleDegreeDifference * it) - 90f) * (PI / 180f)).toFloat()
                val lineLength = radius * 0.90f
                val lineColour = Color.Gray
                val startOffsetLine = Offset(
                    x = lineLength * cos(angleRadDifference) + size.center.x,
                    y = (lineLength * sin(angleRadDifference) + size.center.y)
                )
                val endOffsetLine = Offset(
                    x = ((radius * 0.97f) - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
                    y = ((radius * 0.97f) - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
                )
                drawLine(
                    color = lineColour,
                    start = startOffsetLine,
                    end = endOffsetLine,
                    strokeWidth = radius * .04f
                )
            }

            //seconds hand
            drawCircle(
                color = Color.Gray,
                center = Offset(
                    x = (radius * .80f) * cos(timeRemaining.secondsToRad()) + size.center.x,
                    y = (radius * .80f) * sin(timeRemaining.secondsToRad()) + size.center.y
                ),
                radius = radius * .05f
            )

            drawText(
                text = "size:  ${size.toString()}  min ${minWidth.toString()}",
                textMeasurer = textMeasurer,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black,
                    background = Color.Red.copy(alpha = 0.2f)
                )
            )
        }
    }
}

fun Int.secondsToRad(): Float {
    val angle = (360f / 60f * this) - 90f
    return (angle * (PI / 180f)).toFloat()
}

@Preview
@Composable
fun ClockPreview() {
    TimerTheme {
        Clock(timeRemaining = 10)
    }
}

@Preview
@Composable
fun TimeDisplaySecondPreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 55)
    }
}

@Preview
@Composable
fun TimeDisplaySecondAndMinutePreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 2400)
    }
}

@Preview
@Composable
fun TimeDisplaySecondAndMinuteAndHourPreview() {
    TimerTheme {
        TimeDisplay(timeRemaining = 15200)
    }
}