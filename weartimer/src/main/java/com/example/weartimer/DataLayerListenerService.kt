package com.example.weartimer

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DataLayerListenerService : WearableListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            Log.i(TAG, "onDataChanged for: ${uri.path}")

            when (uri.path) {
                START_TIMER_RECEIVE -> {

                    val timerDuration = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                        .getInt(TIMER_DURATION_KEY)

                    val startTime = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                        .getInt(START_TIMER_TIME_KEY)

                    val secondsAlreadyPassed = ( System.currentTimeMillis().toInt() - startTime ) / 1000
                    val timeRemaining = timerDuration - secondsAlreadyPassed

                    if (timeRemaining < 0 ) return

                    ClockTimer.apply {
                        this.secondsRemaining.value = timeRemaining
                    }

                    Intent(this, TimerService::class.java).also { intent ->
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
        private const val TAG = "DataLayerListenerService"
        const val TIMER_DURATION_KEY = "timerDuration"
        const val START_TIMER_TIME_KEY = "startTime"
        const val START_TIMER_RECEIVE = "/startTimerWear"
        const val PAUSE_TIMER_RECEIVE = "/pauseTimerWear"
        const val RESET_TIMER_RECEIVE = "/resetTimerWear"
        const val START_TIMER_SEND = "/startTimerPhone"
        const val PAUSE_TIMER_SEND = "/pauseTimerPhone"
        const val RESET_TIMER_SEND = "/resetTimerPhone"
    }
}