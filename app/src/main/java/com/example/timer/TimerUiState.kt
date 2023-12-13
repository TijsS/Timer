package com.example.timer


data class TimerUiState (
    val dismissPercentage: Float = 0f,
    var secondInput: Int = 0,
    var minuteInput: Int = 0,
    var hourInput: Int = 0,
    var resetInput: Boolean = false,
)