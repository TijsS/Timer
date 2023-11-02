package com.example.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

const val CHANNEL_ID = "12"
const val CHANNEL_NAME = "Timer Notifications"


//TODO iets is mis met die body
fun Context.showNotification(channelId: String = CHANNEL_ID, title: String = "", body: String = "") {
    val intent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//
//    notificationChannel =
//        NotificationChannel("12", "Timer Notifications", NotificationManager.IMPORTANCE_HIGH)
//
//    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.createNotificationChannel(notificationChannel)

    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentTitle(title)
        .setContentText(body)
//        .setAutoCancel(false)
//        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
//        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setCategory(NotificationCompat.CATEGORY_ALARM)
//        .setOnlyAlertOnce(true)

    Log.d("TAG", "showNotification: ")
    notificationManager.notify(channelId.toInt(), builder.build())
}

fun Context.dismissNotification(channelId: String) {
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(channelId.toInt())
}

// function for updating the contentText of the notification
fun Context.updateNotificationContentText(channelId: String, x: String, newBody: String) {
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Retrieve the existing notification using channelId
    val existingNotification = notificationManager.activeNotifications.find {
        it.id == 12
    }

    if (existingNotification != null) {
        // Modify the contentText of the existing notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(existingNotification.notification.extras.getString(NotificationCompat.EXTRA_TITLE))
            .setContentText(newBody) // Update the contentText
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOnlyAlertOnce(true)


        // Use the same PendingIntent
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        builder.setContentIntent(pendingIntent)

        // Update the notification with the new content
        notificationManager.notify(channelId.toInt(), builder.build())
    }
}
