package com.example.timer.feature_timer.presentation

import androidx.lifecycle.ViewModel
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.data.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val externalScope: CoroutineScope
) : ViewModel() {

    private val coroutineContext: CoroutineContext = externalScope.coroutineContext

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    val _eventFlow = MutableSharedFlow<UiEvent>()
//    val eventFlow = _eventFlow.asSharedFlow()
    suspend fun emit(value: UiEvent) = _eventFlow.emit(value)

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
            .launchIn(externalScope)
    }

    suspend fun removePresetTimer(timerId: Int) {
        timerRepository.removeTimer(timerId = timerId)
    }

    fun startCountDown() {
        println("real =     ${externalScope}")
        println("real =     ${externalScope.coroutineContext}")
        println("realDispatchers.Main =     ${externalScope.coroutineContext}")
        println("realDispatchers.default =     ${Dispatchers.Default}")

        externalScope.launch {
            emit(UiEvent.StartTimer)
            _eventFlow.emit(
                UiEvent.StartTimer
            )
        }
    }

    fun pauseCountDown() {
        externalScope.launch(coroutineContext) {
            _eventFlow.emit(
                UiEvent.PauseTimer
            )
        }
    }

    fun stopCountDown() {
        externalScope.launch(coroutineContext) {
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
        ClockTimer.secondsRemaining.intValue += duration

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
