package com.example.timer

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.CombinedVibration
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import android.window.SplashScreen
import androidx.annotation.RequiresApi
import com.example.timer.DataLayerListenerService.Companion.PAUSE_TIMER
import com.example.timer.DataLayerListenerService.Companion.RESET_TIMER
import com.example.timer.DataLayerListenerService.Companion.START_TIMER
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


@RequiresApi(Build.VERSION_CODES.S)
class TimerService: Service(){

    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }
    private val dataClient by lazy { Wearable.getDataClient(this) }

    private var countDownTimer: CountDownTimer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    private fun notifiedStart() {
        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer(ClockTimer.timeRemaining.intValue.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.intValue -= 1
                applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.intValue.intTimeToString() )
            }

            @RequiresApi(34)
            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished

                vibrate()

                applicationContext.updateNotificationAlarmFinished( 2 )

            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running

        startForeground( NOTIFICATION_ID.toInt(), createNotification(this).build() )
    }

    private fun start () {
        serviceScope.launch {
            try {
                val request = PutDataMapRequest.create(START_TIMER).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.intValue)
                    dataMap.putInt(DataLayerListenerService.START_TIMER_TIME_KEY, System.currentTimeMillis().toInt() )
                }
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()

                return@launch

            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")

            }
        }
        notifiedStart()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun notifiedReset() {
        countDownTimer?.cancel()
        vibratorManager.cancel()
        stopSelf()

        ClockTimer.apply{
            timeRemaining.intValue = 0
            timerState.value = TimerState.Stopped
        }
    }

    private fun reset(){
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(RESET_TIMER)
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedReset()
    }

    private fun notifiedPause(){
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Paused
        applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.intValue.intTimeToString() )
    }

    private fun pause() {
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(PAUSE_TIMER).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.intValue)
                    dataMap.putInt(DataLayerListenerService.START_TIMER_TIME_KEY, System.currentTimeMillis().toInt() )
                }
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedPause()
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
            Action.NotifiedStart.toString() -> notifiedStart()
            Action.Pause.toString() -> pause()
            Action.NotifiedPause.toString() -> notifiedPause()
            Action.Reset.toString() -> reset()
            Action.NotifiedReset.toString() -> notifiedReset()
            Action.Stop.toString() -> {
                reset()
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    enum class Action {
        Start, NotifiedStart, Pause, NotifiedPause, Reset, NotifiedReset, Stop
    }
}