package com.example.weartimer

import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CombinedVibration
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weartimer.DataLayerListenerService.Companion.START_TIMER
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@RequiresApi(Build.VERSION_CODES.S)
class TimerService: Service(){

    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    private var countDownTimer: CountDownTimer? = null
    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: IBinder? = null        // interface for clients that bind
    private var allowRebind: Boolean = true   // indicates whether onRebind should be used
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val dataClient by lazy { Wearable.getDataClient(this) }

    private fun notifiedStart() {
        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer((ClockTimer.timeRemaining.value * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.value -= 1
                applicationContext.updateNotificationContentText( NOTIFICATION_ID.toInt(), ClockTimer.timeRemaining.value.timeRemainingToClockFormat() )
            }

            @RequiresApi(34)
            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished

                vibrate()

                applicationContext.updateNotificationAlarmFinished( 2 )

            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running

//        notificationManager.notify( NOTIFICATION_ID.toInt(), createNotification(this) )
        startForeground( NOTIFICATION_ID.toInt(), createNotification(this) )
    }

    private fun start () {
        serviceScope.launch {
            try {
                val request = PutDataMapRequest.create(START_TIMER).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.value)
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

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }
    override fun onUnbind(intent: Intent): Boolean {
        return allowRebind
    }
    override fun onRebind(intent: Intent) {
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
            Action.NotifiedStart.toString() -> notifiedStart()
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
        Start, Pause, Reset, Stop, NotifiedStart
    }
}