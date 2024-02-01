package com.example.weartimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
class TimerNotificationReceiver: BroadcastReceiver() {

    private fun start(context: Context) {
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.NotifiedStart.toString()
            context.startService(intent)
        }
    }

    private fun pause( context: Context ) {
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.NotifiedPause.toString()
            context.startService(intent)
        }
    }

    private fun reset(context: Context) {
        Log.d("xxxx", "reset: ")
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.NotifiedReset.toString()
            context.startService(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        when(intent?.action) {
            Action.Pause.toString() -> pause( context )
            Action.Reset.toString() -> reset( context )
            Action.Play.toString() -> start( context )
        }
    }

    enum class Action {
        Pause, Reset, Play
    }
}