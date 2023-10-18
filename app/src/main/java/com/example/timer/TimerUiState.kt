package com.example.timer

data class TimerUiState (
    val timeRemaining: Int = 0,
    val timerState: TimerState = TimerState.Paused
    )