package com.example.timer

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Timer


//TODO will probs need to make it a bind service
class TimerService(): Service(){

    private var countDownTimer: CountDownTimer? = null

    private fun start() {
        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer(ClockTimer.timeRemaining.intValue.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.intValue -= 1
                applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.intValue.intTimeToString() )
            }

            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished
//                stopSelf()
            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running

        startForeground( NOTIFICATION_ID.toInt(), createNotification(this).build() )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun reset(){
        countDownTimer?.cancel()

        ClockTimer.apply{
            timeRemaining.intValue = 0
            timerState.value = TimerState.Stopped
        }
    }

    private fun pause(){
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Paused
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.Start.toString() -> start()
            Action.Pause.toString() -> pause()
            Action.Reset.toString() -> reset()
            Action.Stop.toString() -> {
                reset()
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    enum class Action {
        Start, Pause, Reset, Stop
    }
}