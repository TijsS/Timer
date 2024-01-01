package com.example.timer.feature_wearable

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerService
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DataLayerListenerService : WearableListenerService() {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            Log.i(TAG, "onDataChanged for: ${uri.path}")
            when (uri.path) {
                START_TIMER_RECEIVE -> {
                    var timePassedSinceStart = 0
                    Intent(this, TimerService::class.java).also { intent ->
                        DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply {
                            getInt(START_TIMER_TIME_KEY).also { startTime ->
                                timePassedSinceStart = System.currentTimeMillis().toInt() - startTime
                            }
                            getInt(TIMER_DURATION_KEY).also { timerDuration ->
                                ClockTimer.apply {
                                    this.timeRemaining.intValue = timerDuration - ( timePassedSinceStart / 1000 )
                                }
                            }
                        }
                        intent.action = TimerService.Action.NotifiedStart.toString()
                        this.startService(intent)
                    }
                }

                PAUSE_TIMER_RECEIVE -> {
                    Intent(this, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.NotifiedPause.toString()
                        this.startService(intent)
                    }
                }

                RESET_TIMER_RECEIVE -> {
                    Intent(this, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.NotifiedReset.toString()
                        this.startService(intent)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        private const val TAG = "xxx"
        const val TIMER_DURATION_KEY = "timerDuration"
        const val START_TIMER_TIME_KEY = "startTime"
        const val START_TIMER_RECEIVE = "/startTimerPhone"
        const val PAUSE_TIMER_RECEIVE = "/pauseTimerPhone"
        const val RESET_TIMER_RECEIVE = "/resetTimerPhone"
        const val START_TIMER_SEND = "/startTimerWear"
        const val PAUSE_TIMER_SEND = "/pauseTimerWear"
        const val RESET_TIMER_SEND = "/resetTimerWear"
    }
}