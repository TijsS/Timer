package com.example.timer.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.timer.R
import com.example.timer.feature_timer.ClockTimer

@Composable
fun MuteButton() {
    var muteAnimation: RiveAnimationView? = null
    val muteProgress = remember {
        Animatable(250f)
    }

    LaunchedEffect(muteProgress.value) {
        muteAnimation?.setNumberState("StateMachine", "dismissSwipe", muteProgress.value)
    }

    LaunchedEffect(ClockTimer.muted.value) {
        muteProgress.animateTo(
            targetValue = if (ClockTimer.muted.value) 250f else 0f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(35.dp)
    ) {
        ComposableRiveAnimationView(
            animation = R.raw.mute2,
            modifier = Modifier
        ) { view ->
            muteAnimation = view
        }
        Box(modifier = Modifier.fillMaxSize()
            .clickable {
                ClockTimer.muted.value = !ClockTimer.muted.value
            }) {
        }
    }
}