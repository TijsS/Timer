package com.example.timer.feature_wearable

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class DataLayerListenerService : WearableListenerService() {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        dataEvents.forEach { dataEvent ->
            val uri = dataEvent.dataItem.uri
            Log.i(TAG, "onDataChanged for: ${uri.path}")
            when (uri.path) {
                START_TIMER -> {

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