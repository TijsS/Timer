package com.example.timer.feature_timer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var duration: Int,
)

object ClockTimer {
    val timerState: MutableState<TimerState> = mutableStateOf(TimerState.Stopped)
    val secondsRemaining = mutableIntStateOf(0)
    val timerDurationForRepeat = mutableIntStateOf(0)
    val muted = mutableStateOf(false)
}

fun addTimeClockTimer(time: Int) {
    ClockTimer.apply {
        secondsRemaining.intValue += time
        timerDurationForRepeat.intValue += time
    }
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
    object Running : TimerState() // Timer is running
    object Paused : TimerState() // Timer is paused
    object Stopped : TimerState() // Timer is stopped by the user
    object Finished : TimerState() // Timer has finished and is ringing
}