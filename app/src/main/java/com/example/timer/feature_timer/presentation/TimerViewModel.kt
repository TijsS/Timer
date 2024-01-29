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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        getPresetTimers()
    }

    suspend fun addEmptyPresetTimer() {
        timerRepository.addTimer(Timer(name = "", duration = 0))
    }

    suspend fun updatePresetTimer(timer: Timer) {
        timerRepository.updateTimer(timer)
    }

    fun getPresetTimers() {
        timerRepository.getTimersFlow()
            .onEach { timers ->
                _uiState.value = uiState.value.copy(
                    timers = timers
                )
            }
            .launchIn(viewModelScope)
    }

    suspend fun removePresetTimer(timerId: Int) {
        timerRepository.removeTimer(timerId = timerId)
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

    fun addSecondsToTimer(duration: Int) {
        ClockTimer.secondsRemaining.intValue += duration.toInt()
        _uiState.value =
            _uiState.value.copy(resetMainTimeInput = !_uiState.value.resetMainTimeInput)

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
