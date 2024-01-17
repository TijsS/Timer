package com.example.timer.feature_timer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Timer(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val name: String,
    val duration: Long,
)

object ClockTimer {
    var timerState: MutableState<TimerState> = mutableStateOf(TimerState.Stopped)
    var millisRemaining = mutableIntStateOf(0)
}

fun Int.intTimeToString(): String {

    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    var string = ""

    if (hours > 0) {
        string += "$hours:"
    }

    return string + String.format("%02d:%02d", minutes, seconds)
}
sealed class TimerState {
    object Running : TimerState()
    object Paused : TimerState()
    object Stopped : TimerState()
    object Finished : TimerState()
}