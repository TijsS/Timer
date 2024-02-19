package com.example.timer.feature_timer.presentation

import androidx.lifecycle.ViewModel
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.addTimeClockTimer
import com.example.timer.feature_timer.data.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    private val timerRepository: TimerRepository,
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getPresetTimers()
    }

    private fun getPresetTimers() {
        timerRepository.getTimersFlow()
            .onEach { timers ->
                _uiState.value = uiState.value.copy(
                    timers = timers
                )
            }
            .launchIn(externalScope)
    }

    suspend fun addEmptyPresetTimer() {
        timerRepository.addTimer(Timer(name = "", duration = 0))
    }

    suspend fun updatePresetTimer(timer: Timer) {
        timerRepository.updateTimer(timer)
    }

    suspend fun removePresetTimer(timerId: Int) {
        timerRepository.removeTimer(timerId = timerId)
    }

    fun startCountDown() {
        externalScope.launch {
            _eventFlow.emit(
                UiEvent.StartTimer
            )
        }
    }

    fun pauseCountDown() {
        externalScope.launch {
            _eventFlow.emit(
                UiEvent.PauseTimer
            )
        }
    }

    fun resetCountDown() {
        externalScope.launch {
            _eventFlow.emit(
                UiEvent.ResetTimer
            )
        }
    }
    fun repeatCountDown() {
        externalScope.launch {
            _eventFlow.emit(
                UiEvent.RepeatTimer
            )
        }
    }

    fun setDismissPercentage(percentage: Float) {
        _uiState.value =
            _uiState.value.copy(dismissPercentage = if (percentage < -100) -100f else if (percentage > 100) 100f else percentage)
    }

    fun addSecondsToTimer(duration: Int) {

        addTimeClockTimer(duration)

        _uiState.value =
            _uiState.value.copy(resetMainTimeInput = !_uiState.value.resetMainTimeInput)

        if (ClockTimer.timerState.value == TimerState.Running) {
            startCountDown()
        }
    }

    sealed class UiEvent {
        data object RepeatTimer : UiEvent()
        data object StartTimer : UiEvent()
        data object PauseTimer : UiEvent()
        data object ResetTimer : UiEvent()
    }
}
