package com.example.timer.feature_timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.data.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getTimers()
        }
    }

    suspend fun addTimer() {
        timerRepository.addTimer(Timer(name = "", duration = 0))
        getTimers()
    }

    suspend fun updateTimer(timer: Timer) {
        timerRepository.updateTimer(timer)
        getTimers()
    }

    suspend fun getTimers() {
        timerRepository.getTimersFlow().collectLatest {
            _uiState.value = _uiState.value.copy(timers = it)
        }
    }

    suspend fun removeTimer(timerId: Int) {
        timerRepository.removeTimer(timerId = timerId)
        getTimers()
    }

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
        _uiState.value =
            _uiState.value.copy(dismissPercentage = if (percentage < 0) 0f else if (percentage > 100) 100f else percentage)
    }

    fun addSecondsToTimerFromPreset(duration: Long) {
        ClockTimer.millisRemaining.intValue += duration.toInt()
        _uiState.value = _uiState.value.copy(resetInput = !_uiState.value.resetInput)

        if (ClockTimer.timerState.value == TimerState.Running) {
            startCountDown()
        }
    }

    sealed class UiEvent {
        data object StopTimer : UiEvent()
        data object StartTimer : UiEvent()
        data object PauseTimer : UiEvent()
        data object ResetTimer : UiEvent()
    }
}
