package com.example.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

const val CHANNEL_ID = "13"
const val NOTIFICATION_ID = "2"
const val CHANNEL_NAME = "Timer Notifications"

fun createNotification(context : Context): NotificationCompat.Builder {
    val activityIntent = Intent(context, MainActivity::class.java)

    val activityPendingIntent = PendingIntent.getActivity(
        context,
        2,
        activityIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val pauseIntent = Intent(context, TimerNotificationReceiver::class.java)
    pauseIntent.action = TimerNotificationReceiver.Action.Pause.toString()

    val pausePendingIntent = PendingIntent.getBroadcast(
        context,
        2,
        pauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val resetIntent = Intent(context, TimerNotificationReceiver::class.java)
    resetIntent.action = TimerNotificationReceiver.Action.Reset.toString()

    val resetPendingIntent = PendingIntent.getBroadcast(
        context,
        2,
        resetIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Timer")
        .setContentText("")
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentIntent(activityPendingIntent)
        .setOnlyAlertOnce(true)
        .addAction(
            R.drawable.baseline_pause_24,
            "Pause",
            pausePendingIntent
        )
        .addAction(
            R.drawable.baseline_autorenew_24,
            "Reset",
            resetPendingIntent
        )
}

fun Context.dismissNotification(channelId: String) {
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(channelId.toInt())
}

// function for updating the contentText of the notification
fun Context.updateNotificationContentText(id: Int, newBody: String) {

    val pauseIntent = Intent(this, TimerNotificationReceiver::class.java)
    pauseIntent.action = TimerNotificationReceiver.Action.Pause.toString()

    val pausePendingIntent = PendingIntent.getBroadcast(
        this,
        2,
        pauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val resetIntent = Intent(this, TimerNotificationReceiver::class.java)
    resetIntent.action = TimerNotificationReceiver.Action.Reset.toString()

    val resetPendingIntent = PendingIntent.getBroadcast(
        this,
        2,
        resetIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val playIntent = Intent(this, TimerNotificationReceiver::class.java)
    playIntent.action = TimerNotificationReceiver.Action.Play.toString()

    val playPendingIntent = PendingIntent.getBroadcast(
        this,
        2,
        playIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val existingNotification = notificationManager.activeNotifications.find {
        it.id == id
    }

    if (existingNotification != null) {
        // Modify the contentText of the existing notification
         val builder = createNotification(this)
             .setContentText(newBody)
             .clearActions()

        if (ClockTimer.timerState.value == TimerState.Paused) {
            builder.addAction (
                R.drawable.baseline_play_arrow_24,
                "Play",
                playPendingIntent
            )
        } else {
            builder.addAction(
                R.drawable.baseline_pause_24,
                "Pause",
                pausePendingIntent
            )
        }
        builder.addAction(
            R.drawable.baseline_autorenew_24,
            "Reset",
            resetPendingIntent
        )

        // Update the notification with the new content
        notificationManager.notify(id, builder.build())
    }
}
