package com.example.timer.feature_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.timer.MainActivity
import com.example.timer.R
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerNotificationReceiver
import com.example.timer.feature_timer.TimerState


lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

lateinit var activityPendingIntent: PendingIntent
lateinit var resetPendingIntent: PendingIntent
lateinit var pausePendingIntent: PendingIntent


const val CHANNEL_ID = "13"
const val NOTIFICATION_ID = "2"
const val CHANNEL_NAME = "Timer Notifications"
const val REQUEST_CODE = 2

@RequiresApi(Build.VERSION_CODES.S)
fun createNotification(context: Context): NotificationCompat.Builder {
    val activityIntent = Intent(context, MainActivity::class.java)

    activityPendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        activityIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val pauseIntent = Intent(context, TimerNotificationReceiver::class.java)
    pauseIntent.action = TimerNotificationReceiver.Action.Pause.toString()

     pausePendingIntent = PendingIntent.getBroadcast(
        context,
         REQUEST_CODE,
        pauseIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val resetIntent = Intent(context, TimerNotificationReceiver::class.java)
    resetIntent.action = TimerNotificationReceiver.Action.Reset.toString()

    resetPendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        resetIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Timer")
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentIntent(activityPendingIntent)
        .setSilent(true)
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

fun dismissNotification(channelId: String) {
    notificationManager.cancel(channelId.toInt())
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.updateNotificationContentText(newBody: String) {

    val playIntent = Intent(this, TimerNotificationReceiver::class.java)
    playIntent.action = TimerNotificationReceiver.Action.Play.toString()

    val playPendingIntent = PendingIntent.getBroadcast(
        this,
        REQUEST_CODE,
        playIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val existingNotification = notificationManager.activeNotifications.find {
        it.id == CHANNEL_ID.toInt()
    }

    if (existingNotification != null) {
        val builder = createNotification(this)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentText(newBody)
            .clearActions()

        if (ClockTimer.timerState.value == TimerState.Paused) {
            builder.addAction(
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
        notificationManager.notify(CHANNEL_ID.toInt(), builder.build())
    }
}

@RequiresApi(34)
fun Context.updateNotificationAlarmFinished(
) {
    val existingNotification = notificationManager.activeNotifications.find {
        it.id == CHANNEL_ID.toInt()
    }

    if (existingNotification != null) {
        dismissNotification(CHANNEL_ID)
    }

    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Timer")
        .setContentText("Time's up!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(null)
        .setFullScreenIntent(activityPendingIntent, true)
        .clearActions()
        .addAction(
            R.drawable.baseline_autorenew_24,
            "Stop",
            resetPendingIntent
        )

    notificationManager.notify(NOTIFICATION_ID.toInt(), builder.build())
}
