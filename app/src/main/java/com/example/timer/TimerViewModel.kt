package com.example.timer

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Timer


class TimerViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val timer = Timer()

    private var countDownTimer: CountDownTimer? = null

    fun startCountDown() {
        countDownTimer?.cancel() // Cancel any existing timers

        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.StartTimer
            )
        }

        countDownTimer = object : CountDownTimer(_uiState.value.timeRemaining.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value =
                    _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining - 1)
            }

            override fun onFinish() {
                //implement vibrate
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.TimerFinished
                    )
                }

                _uiState.value = _uiState.value.copy(timerState = TimerState.Finished)
            }
        }.start()

        _uiState.value = _uiState.value.copy(timerState = TimerState.Running)
    }

    fun cancelCountDown() {
        countDownTimer?.cancel()
        _uiState.value = _uiState.value.copy(timerState = TimerState.Stopped)
    }

    fun setDismissPercentage(percentage: Float) {
        _uiState.value = _uiState.value.copy(dismissPercentage = if( percentage < 0f ) 0f else if( percentage > 100 ) 100f else percentage)

    }

    fun addSecondsToTimer(seconds: Int) {
        _uiState.value = _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining + seconds)

        if (_uiState.value.timerState == TimerState.Running) {
            startCountDown()
        }
    }

    fun resetCountDown() {
        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.AlarmStopped
            )
        }

        _uiState.value = _uiState.value.copy(
            timeRemaining = 0,
            dismissPercentage = 0f,
            timerState = TimerState.Stopped
        )
        countDownTimer?.cancel()
    }

    sealed class UiEvent {
        object TimerFinished: UiEvent()
        object StartTimer : UiEvent()
        object AlarmStopped: UiEvent()
    }
}
