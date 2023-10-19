package com.example.timer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timer.ui.theme.TimerTheme
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimeDisplay(timeRemaining: Int, modifier: Modifier = Modifier) {

    var secondVisibility by remember { mutableStateOf(true) }
    var minuteVisibility by remember { mutableStateOf(false) }
    var hourVisibility by remember { mutableStateOf(false) }

    val largeTargetDp =  360.dp.value
    val mediumTargetDp = 250.dp.value
    val smallTargetDp = 130.dp.value
    val reallySmallTargetDp = 0.dp.value

    val secondDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
    val minuteDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }
    val hourDp by remember { mutableStateOf(Animatable(reallySmallTargetDp)) }

    val secondRotation by remember { mutableStateOf(Animatable(0f)) }


    val minuteRotation by remember { mutableStateOf(Animatable(0f)) }
    val hourRotation by remember { mutableStateOf(Animatable(0f)) }

    LaunchedEffect(timeRemaining) {
        launch {
            secondRotation.animateTo(
                timeRemaining * 6f,
                animationSpec = tween(
                    delayMillis = 0,
                    durationMillis = 0
                )
            )
        }
    }

    when {
        timeRemaining > 3600 -> {
            LaunchedEffect(timeRemaining) {
                launch {
                    secondDp.animateTo(
                        smallTargetDp,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    minuteDp.animateTo(
                        smallTargetDp,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    hourDp.animateTo(
                        largeTargetDp,
                        animationSpec = tween(
                            durationMillis = 500,
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
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }

                launch {
                    minuteDp.animateTo(
                        mediumTargetDp,
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
                            durationMillis = 500,
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
        if(minuteDp.value.dp > 0.dp) {
            Clock(
                timeRemaining = (timeRemaining / 3600) % 60,
                rotate = 0f,
                clockSize = hourDp.value.dp,
                modifier = Modifier
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
//            if(minuteDp.value.dp > 0.dp) {
//                Clock(
//                    timeRemaining = (timeRemaining / 60) % 60,
//                    rotate = 0f,
//                    clockSize = minuteDp.value.dp,
//                    modifier = Modifier
//                )
//            }

            Clock(
                timeRemaining = timeRemaining % 60,
                rotate = secondRotation.value,
                clockSize = secondDp.value.dp,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun Clock(
    timeRemaining: Int,
    rotate: Float,
    clockSize: Dp = 128.dp,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
//Text(text = rotate.toString())
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints {
        Canvas(
            modifier = modifier
            .requiredSize(clockSize)
        ) {
            val radius = size.width * .45f

            rotate(rotate) {
                drawCircle(
                    color = primaryColor,
                    style = Stroke(width = radius * .015f),
                    radius = radius,
                    center = size.center,
                )
                val (opacityLeft, opacityRight) = if( (rotate.toInt()/360) % 2 == 0 ) 0.1f to 1f else 1f to 0.1f

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
                        x = ((radius * 0.97f) - ((radius * .05f) / 2) ) * cos(angleRadDifference) + size.center.x,
                        y = ((radius * 0.97f) - ((radius * .05f) / 2) ) * sin(angleRadDifference) + size.center.y
                    )
                    drawLine(
                        color = primaryColor.copy(alpha = if(it in (60-timeRemaining)..60) opacityRight else opacityLeft),
                        start = startOffsetLine,
                        end = endOffsetLine,
                        strokeWidth = radius * .05f,

                        //if (it in (60 - timeRemaining)..60) opacityLeft else opacityRight))
                        //if(it in (60-timeRemaining)..(65-timeRemaining)) 1f else if (it in (55-timeRemaining)..60) 0.5f else 0.1f))
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${timeRemaining}  ${opacityRight}  ${it in (60-timeRemaining)..60}",
                        topLeft = Offset(
                            x = center.x - 52f,
                            y = center.y,
                        ),
                    )
                }

                drawCircle(
                    color = primaryColor,
                    center = Offset(
                        x = size.center.x,
                        y = (radius * .80f) * -1 + size.center.y
                    ),
                    radius = radius * .05f
                )
            }
        }
    }
}

//fun Int.secondsToRad(): Float {
//    val angle = (360f / 60f * this) - 90f
//    return (angle * (PI / 180f)).toFloat()
//}

@Preview
@Composable
fun ClockPreview() {
    TimerTheme {
        Clock(timeRemaining = 15, rotate = 90f)
    }
}
@Preview
@Composable
fun Clock1minPreview() {
    TimerTheme {
        Clock(timeRemaining = 75, rotate = 450f)
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