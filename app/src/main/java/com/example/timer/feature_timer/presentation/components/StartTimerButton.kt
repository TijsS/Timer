package com.example.timer.feature_timer.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.timer.R

@Composable
fun StartTimerButton(
    modifier: Modifier = Modifier,
    timerGreaterThenZero: () -> Boolean,
    startTimer: () -> Unit,
) {
    IconButton(
        onClick = { startTimer() },
        enabled = timerGreaterThenZero(),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_play_arrow_24),
            contentDescription = "Start timer"
        )
    }
}