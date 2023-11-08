package com.example.timer

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class TimerViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var countDownTimer: CountDownTimer? = null

    fun startCountDown() {
        countDownTimer?.cancel() // Cancel any existing timers

        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.StartTimer
            )
        }

        countDownTimer = object : CountDownTimer(ClockTimer.timeRemaining.intValue.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.intValue -= 1

            }

            override fun onFinish() {
                //implement vibrate
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.TimerFinished
                    )
                }

                ClockTimer.timerState.value = TimerState.Finished
            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running
    }

    fun cancelCountDown() {
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Stopped
    }

    fun setDismissPercentage(percentage: Float) {
        _uiState.value = _uiState.value.copy(dismissPercentage = if( percentage < 0f ) 0f else if( percentage > 100 ) 100f else percentage)

    }

    fun addSecondsToTimer(seconds: Int) {

        ClockTimer.timeRemaining.value += seconds

        if (ClockTimer.timerState.value == TimerState.Running) {
            startCountDown()
        }
    }

    fun resetCountDown() {
        viewModelScope.launch {
            _eventFlow.emit(
                UiEvent.AlarmStopped
            )
        }

        ClockTimer.apply{
            timeRemaining.intValue = 0
            timerState.value = TimerState.Stopped
        }
        countDownTimer?.cancel()
    }

    sealed class UiEvent {
        object TimerFinished: UiEvent()
        object StartTimer : UiEvent()
        object AlarmStopped: UiEvent()
    }
}
