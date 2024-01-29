package com.example.timer.feature_timer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Timer(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    var name: String,
    var duration: Int,
)

object ClockTimer {
    var timerState: MutableState<TimerState> = mutableStateOf(TimerState.Stopped)
    var secondsRemaining = mutableIntStateOf(0)
}

fun Int.intTimeToString(): String {

    val hours = this.toHours()

    var string = ""

    if (hours > 0) {
        string += "$hours:"
    }

    return string + String.format("%02d:%02d", this.toMinutes(), this.toSeconds())
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

sealed class TimerState {
    object Running : TimerState()
    object Paused : TimerState()
    object Stopped : TimerState()
    object Finished : TimerState()
}