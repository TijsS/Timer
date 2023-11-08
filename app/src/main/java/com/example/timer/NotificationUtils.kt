package com.example.timer

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

const val CHANNEL_ID = "13"
const val NOTIFICATION_ID = "2"
const val CHANNEL_NAME = "Timer Notifications"

fun createNotification(context : Context): NotificationCompat.Builder {
    val activityIntent = Intent(context, MainActivity::class.java)
    activityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    
    val activityPendingIntent = PendingIntent.getActivity(
        context,
        2,
        activityIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val actionIntent = PendingIntent.getBroadcast(
        context,
        2,
        Intent(context, TimerNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("My Foreground Service")
        .setContentText("Service is running ${ClockTimer.timeRemaining.intValue}")
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentIntent(activityPendingIntent)
        .setOnlyAlertOnce(true)
        .addAction(
            R.drawable.baseline_stop_24,
            "Stop",
            actionIntent
        )
}

fun Context.dismissNotification(channelId: String) {
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(channelId.toInt())
}

// function for updating the contentText of the notification
fun Context.updateNotificationContentText(id: Int, newBody: String) {

    val existingNotification = notificationManager.activeNotifications.find {
        it.id == id
    }

    if (existingNotification != null) {

        // Modify the contentText of the existing notification
         val builder = createNotification(this)
            .setContentText(newBody)

        // Update the notification with the new content
        notificationManager.notify(id, builder.build())
    }
}
