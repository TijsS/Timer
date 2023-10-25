package com.example.timer

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import java.util.TimerTask


class TimerViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    val timer = Timer()

    private var countDownTimer: CountDownTimer? = null
    val x: Int? = null



    fun startCountDown() {

        x.let { Log.d("TimerViewModel", "x is $it") }

        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer(_uiState.value.timeRemaining.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value =
                    _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining - 1)
            }

            override fun onFinish() {
                _uiState.value = _uiState.value.copy(timerState = TimerState.Finished)
            }
        }.start()

        _uiState.value = _uiState.value.copy(timerState = TimerState.Running)
    }

    fun cancelCountDown() {
        countDownTimer?.cancel()
        _uiState.value = _uiState.value.copy(timerState = TimerState.Stopped)
    }

    fun addSecondsToTimer(seconds: Int) {
        _uiState.value = _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining + seconds)
    }

    fun resetCountDown() {
        _uiState.value = _uiState.value.copy(
            timeRemaining = 0,
            timerState = TimerState.Stopped
        )
        countDownTimer?.cancel()
    }
}
