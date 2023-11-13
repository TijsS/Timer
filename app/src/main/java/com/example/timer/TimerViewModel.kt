package com.example.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun addSecondsToTimer(seconds: Int) {

        ClockTimer.timeRemaining.intValue += seconds

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
