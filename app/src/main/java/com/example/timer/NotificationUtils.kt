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


//TODO iets is mis met die body
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.showNotification(channelId: String = CHANNEL_ID, title: String = "", body: String = "") {
    val intent = Intent(this, TimerViewModel::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)


    val actionIntent = PendingIntent.getBroadcast(
        this,
        2,
        Intent(this, TimerNotificationReceiver::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentTitle(title)
        .setContentText(body)
//        .setAutoCancel(false)
//        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(
            androidx.core.R.drawable.ic_call_answer,
            "Stop",
            actionIntent
        )
//        .setCategory(NotificationCompat.CATEGORY_ALARM)
//        .setOnlyAlertOnce(true)

    notificationManager.notify(CHANNEL_ID.toInt(), builder.build())
}

fun createNotification(context : Context): NotificationCompat.Builder {
    val activityIntent = Intent(context, MainActivity::class.java)
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
    Log.d("xxx", "updateNotificationContentText: ${newBody}")

    val existingNotification = notificationManager.activeNotifications.find {
        it.id == id
    }

    if (existingNotification != null) {
        // Modify the contentText of the existing notification
//        val builder = NotificationCompat.Builder(this, id.toString())
//            .setSmallIcon(R.drawable.baseline_alarm_24)
//            .setContentTitle(existingNotification.notification.extras.getString(NotificationCompat.EXTRA_TITLE))
//            .setContentText(newBody) // Update the contentText
//            .setAutoCancel(false)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setCategory(NotificationCompat.CATEGORY_ALARM)
//            .setOnlyAlertOnce(true)
         val builder = createNotification(this)
            .setContentText(newBody)

//
//        // Use the same PendingIntent
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
//        builder.setContentIntent(pendingIntent)

        // Update the notification with the new content
        notificationManager.notify(id, builder.build())
    }
}
