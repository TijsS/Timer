package com.example.timer.feature_timer.presentation

import com.example.timer.feature_timer.Timer


data class TimerUiState (
    val dismissPercentage: Float = 0f,
    var resetInput: Boolean = false,
    var timers: List<Timer> = emptyList()
)