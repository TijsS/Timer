package com.example.weartimer

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DataLayerListenerService : WearableListenerService() {
//    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDataChanged(dataEvents: DataEventBuffer) {
//        super.onDataChanged(dataEvents)

        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            Log.i(TAG, "onDataChanged for: ${uri.path}")

            when (uri.path) {
                START_TIMER -> {

                    val timerDuration = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                        .getInt(TIMER_DURATION_KEY)

                    val startTime = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                        .getInt(START_TIMER_TIME_KEY)

                    val secondsAlreadyPassed = ( System.currentTimeMillis().toInt() - startTime ) / 1000
                    val timeRemaining = timerDuration - secondsAlreadyPassed

                    if (timeRemaining < 0 ) return

                    ClockTimer.apply {
                        this.timeRemaining.value = timeRemaining
                    }


                    Intent(this@DataLayerListenerService, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Start.toString()
                        this@DataLayerListenerService.startService(intent)
                    }
                }

                PAUSE_TIMER -> {
                    Intent(this@DataLayerListenerService, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Pause.toString()
                        this@DataLayerListenerService.startService(intent)
                    }
                }

                RESET_TIMER -> {
                    Intent(this@DataLayerListenerService, TimerService::class.java).also { intent ->
                        intent.action = TimerService.Action.Reset.toString()
                        this@DataLayerListenerService.startService(intent)
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
        const val START_TIMER = "/startTimer"
        const val TIMER_DURATION_KEY = "timerDuration"
        const val START_TIMER_TIME_KEY = "startTime"
        const val PAUSE_TIMER = "/pauseTimer"
        const val RESET_TIMER = "/resetTimer"
    }
}