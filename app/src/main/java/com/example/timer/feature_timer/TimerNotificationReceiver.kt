package com.example.timer.feature_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
@RequiresApi(Build.VERSION_CODES.S)
class TimerNotificationReceiver: BroadcastReceiver() {

    private fun start(context: Context) {
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.Start.toString()
            context.startService(intent)
        }
    }

    private fun pause( context: Context ) {
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.Pause.toString()
            context.startService(intent)
        }
    }

    private fun reset(context: Context) {
        Intent(context, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.Reset.toString()
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