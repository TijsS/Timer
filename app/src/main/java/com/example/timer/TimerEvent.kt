package com.example.timer

sealed class TimerEvent {
    data class vibrate( val duration: Long ): TimerEvent()
    object vibrateStop: TimerEvent()
}