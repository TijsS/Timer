package com.example.timer

data class TimerUiState (
    val timeRemaining: Int = 0,
    val timerState: TimerState = TimerState.Paused,
    val dismissPercentage: Float = 0f
    )

fun Int.intTimeToString(): String {
    val hours = this.toInt() / 3600
    val minutes = (this.toInt() % 3600) / 60
    val seconds = this.toInt() % 60

    var string = ""

    if (hours > 0) {
        string += string.format("%02d:", hours)
    }

    return string.format("%02d:%02d", minutes, seconds)
}