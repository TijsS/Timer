package com.example.timer.feature_timer.presentation.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.example.timer.R
import com.example.timer.components.ComposableRiveAnimationView

@Composable
fun TimerFinished(
    setDismissPercentage: (Float) -> Unit,
    resetDismissPercentage: () -> Unit,
    setAlarmAnimation: (RiveAnimationView) -> Unit
) {
    Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
    .fillMaxSize()
    .draggable(
    orientation = Orientation.Vertical,
    state = rememberDraggableState { delta ->
        setDismissPercentage(delta)
    },
    onDragStopped = {
        resetDismissPercentage()
    }
    ),
    ) {
        BoxWithConstraints {
            ComposableRiveAnimationView(
                animation = R.raw.alarm_v3,
                stateMachineName = "StateMachine",
                fit = Fit.COVER,
                modifier = Modifier
                    .size(maxWidth * 0.7f, maxHeight * 0.7f)
            ) { view ->
                setAlarmAnimation(view)
                view.fireState("StateMachine", "startRinging")
            }
        }
    }
}