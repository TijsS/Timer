package com.example.timer.feature_timer.presentation

sealed class TimerEvent {
    data class vibrate( val duration: Long ): TimerEvent()
    object vibrateStop: TimerEvent()
    object startTimer: TimerEvent()
    object StopTimer: TimerViewModel.UiEvent()
    object StartTimer : TimerViewModel.UiEvent()
    object PauseTimer: TimerViewModel.UiEvent()
    object ResetTimer: TimerViewModel.UiEvent()
}