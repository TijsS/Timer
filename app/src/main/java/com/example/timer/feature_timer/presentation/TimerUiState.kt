package com.example.timer.feature_timer.presentation

import com.example.timer.feature_timer.Timer


data class TimerUiState (
    val dismissPercentage: Float = 0f,
    val resetMainTimeInput: Boolean = false,
    val timers: List<Timer> = emptyList()
)