package com.example.weartimer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.CombinedVibration
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.VibratorManager
import android.window.SplashScreen
import androidx.annotation.RequiresApi


@RequiresApi(Build.VERSION_CODES.S)
class TimerService: Service(){

    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    private var countDownTimer: CountDownTimer? = null

    private fun start() {
        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer((ClockTimer.timeRemaining.value * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.value -= 1
                applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.value.intTimeToString() )
            }

            @RequiresApi(34)
            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished

                vibrate()

                applicationContext.updateNotificationAlarmFinished( 2 )

            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running

        startForeground( 12, createNotification(this).build() )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun reset(){
        countDownTimer?.cancel()
        vibratorManager.cancel()
        stopSelf()

        ClockTimer.apply{
            timeRemaining.value = 0
            timerState.value = TimerState.Stopped
        }
    }

    private fun pause(){
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Paused
    }

    private fun vibrate() {
        vibratorManager.vibrate(
            CombinedVibration.createParallel(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 200, 600, 500),
                    intArrayOf(0, 255, 55, 0),
                    1
                )
            )
        )
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