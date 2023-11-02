package com.example.timer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class TimerApp: Application() {

        override fun onCreate() {
            super.onCreate()

            notificationChannel =
                NotificationChannel("timer_channel", "Timer Notifications", NotificationManager.IMPORTANCE_HIGH)

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
}