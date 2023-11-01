package com.example.timer

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Timer


//TODO will probs need to make it a bind service
class TimerService : Service(){



    fun start( seconds: Int) {
        Log.d("Timer Service", "start: Service")
        this.showNotification("12", "Timer", seconds.intTimeToString())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun addSecondsToTimer() {

    }


    fun reset(){
//        this.showNotification("12", "Timer", timer.timeRemaining.toString())
    }

    fun stop(){
        this.dismissNotification("12")
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.Start.toString() -> start( intent.getIntExtra("seconds", 0))
            Action.Stop.toString() -> stop()
            Action.Reset.toString() -> reset()
            Action.AddSecondsToTimer.toString() -> addSecondsToTimer()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    enum class Action {
        Start, Stop, Reset, Update, AddSecondsToTimer
    }
}