package com.example.timer



// states available to timer
sealed class TimerState {
    object Running : TimerState()
    object Paused : TimerState()
    object Stopped : TimerState()
    object Finished : TimerState()
}

//object ClockTimer {
//    var timerState: TimerState = TimerState.Stopped
//    var timeRemaining: Int = 0
//    var dismissPercentage: Float = 0f
//}