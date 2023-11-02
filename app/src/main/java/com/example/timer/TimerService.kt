package com.example.timer

import android.app.Notification.EXTRA_NOTIFICATION_ID
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Timer


//TODO will probs need to make it a bind service
class TimerService : Service(){

    private fun start(seconds: Int): Int {

//        this.showNotification("12", "Timer", seconds.intTimeToString())
        Log.d("TAG", "start: $seconds")

        val notificationIntent = Intent(this, TimerService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Foreground Service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .addAction(androidx.core.R.drawable.ic_call_answer, "Pause", pendingIntent)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun addSecondsToTimer() {

    }


    private fun reset(){
//        this.showNotification("12", "Timer", timer.timeRemaining.toString())
    }

    private fun stop(){
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