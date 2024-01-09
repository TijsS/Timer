package com.example.timer.feature_timer.presentation

import com.example.timer.feature_timer.Timer


data class TimerUiState (
    val dismissPercentage: Float = 0f,
    var secondInput: Int = 0,
    var minuteInput: Int = 0,
    var hourInput: Int = 0,
    var resetInput: Boolean = false,
    var timers: List<Timer> = emptyList()
)