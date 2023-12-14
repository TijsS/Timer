package com.example.timer.feature_timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class TimerViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun startCountDown() {
        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.StartTimer
            )
        }
    }

    fun pauseCountDown() {
        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.PauseTimer
            )
        }

        ClockTimer.timerState.value = TimerState.Paused
    }

    fun stopCountDown() {
        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.StopTimer
            )
        }
    }

    fun setDismissPercentage(percentage: Float) {
        _uiState.value = _uiState.value.copy(dismissPercentage = if( percentage < 0f ) 0f else if( percentage > 100 ) 100f else percentage)

    }

    fun setSecondInput(second: Int) {
        _uiState.value = _uiState.value.copy(secondInput = second)
    }

    fun setMinuteInput(minute: Int) {
        _uiState.value = _uiState.value.copy(minuteInput = minute)
    }

    fun setHourInput(hour: Int) {
        _uiState.value = _uiState.value.copy(hourInput = hour)
    }

    fun addSecondsToTimer() {
        val seconds = _uiState.value.secondInput + _uiState.value.minuteInput * 60 + _uiState.value.hourInput * 3600

        ClockTimer.timeRemaining.intValue += seconds
        _uiState.value = _uiState.value.copy(resetInput = !_uiState.value.resetInput)

        if (ClockTimer.timerState.value == TimerState.Running) {
            startCountDown()
        }
    }

    sealed class UiEvent {
        object StopTimer: UiEvent()
        object StartTimer : UiEvent()
        object PauseTimer: UiEvent()
        object ResetTimer: UiEvent()
    }
}
