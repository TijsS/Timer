package com.example.weartimer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


// states available to timer
sealed class TimerState {
    object Running : TimerState()
    object Paused : TimerState()
    object Stopped : TimerState()
    object Finished : TimerState()
}

object ClockTimer {
    var timerState: MutableState<TimerState> = mutableStateOf(TimerState.Stopped)
    var secondsRemaining = mutableStateOf(0)
}

fun Int.timeRemainingToClockFormat(): String {

    val hours = this.toHours()
    val minutes = this.toMinutes()
    val seconds = this.toSeconds()

    var string = ""

    if (hours > 0) {
        string += "$hours:"
    }

    return string + String.format("%02d:%02d", minutes, seconds)
}

fun Int.timeRemainingToClockFormatWithoutSeconds(): String {

    val hours = this.toHours()
    val minutes = this.toMinutes()

    var string = ""

    if (hours > 0) {
        string += "$hours:"
    }

    return string + String.format("%02d", minutes)
}

fun Int.toHours(): Int {
    return this / 3600
}

fun Int.toMinutes(): Int {
    return (this % 3600) / 60
}

fun Int.toSeconds(): Int {
    return this % 60
}