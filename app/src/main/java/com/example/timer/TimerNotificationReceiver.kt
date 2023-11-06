package com.example.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val service = TimerService( )
//        service.showNotification( )
    }
}