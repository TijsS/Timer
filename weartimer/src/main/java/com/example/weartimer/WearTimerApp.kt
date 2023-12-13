package com.example.weartimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class WearTimerApp: Application() {
    override fun onCreate() {
        super.onCreate()

        notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}