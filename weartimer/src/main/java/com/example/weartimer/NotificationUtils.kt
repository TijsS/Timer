package com.example.weartimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.example.weartimer.presentation.MainActivity


lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

lateinit var activityPendingIntent: PendingIntent
lateinit var resetPendingIntent: PendingIntent
lateinit var pausePendingIntent: PendingIntent
lateinit var ongoingActivity: OngoingActivity

const val CHANNEL_ID = "13"
const val NOTIFICATION_ID = "13"
const val CHANNEL_NAME = "Timer Notifications"
const val REQUEST_CODE = 2

@RequiresApi(Build.VERSION_CODES.S)
fun createNotification(context : Context): Notification {
    val activityIntent = Intent(context, MainActivity::class.java)

    activityPendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        activityIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notificationCompatBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setOngoing(true)

    val ongoingActivityStatus = Status.Builder()
        // Sets the text used across various surfaces.
        .addTemplate(ClockTimer.secondsRemaining.value.timeRemainingToClockFormatWithoutSeconds())
        .build()

    ongoingActivity =
        OngoingActivity.Builder(context, NOTIFICATION_ID.toInt(), notificationCompatBuilder)
            .setAnimatedIcon(R.drawable.baseline_alarm_24)
            .setStaticIcon(R.drawable.baseline_alarm_24)
            .setTouchIntent(activityPendingIntent)
            .setStatus(ongoingActivityStatus)
            .build()

    ongoingActivity.apply(context)

    return notificationCompatBuilder.build()
}

fun dismissNotification(channelId: String) {
    notificationManager.cancel(channelId.toInt())
}

@RequiresApi(34)
fun Context.updateNotificationAlarm() {
    val ongoingActivityStatus = Status.Builder()
        // Sets the text used across various surfaces.
        .addTemplate(ClockTimer.secondsRemaining.value.timeRemainingToClockFormat())
        .build()

    ongoingActivity.update(this, ongoingActivityStatus)
}

@RequiresApi(34)
fun Context.updateNotificationAlarmFinished(
    id: Int,
) {

    val resetIntent = Intent(this, TimerNotificationReceiver::class.java)
    resetIntent.action = TimerNotificationReceiver.Action.Reset.toString()

    resetPendingIntent = PendingIntent.getBroadcast(
        this,
        REQUEST_CODE,
        resetIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val existingNotification = notificationManager.activeNotifications.find {
        it.id == id
    }

    if (existingNotification != null) {
        dismissNotification(id.toString())
    }

    val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Timer")
        .setContentText("Time's up!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setFullScreenIntent(activityPendingIntent, true)
        .clearActions()
        .addAction(
            R.drawable.baseline_autorenew_24,
            "Stop",
            resetPendingIntent
        )

    notificationManager.notify(NOTIFICATION_ID.toInt(), builder.build())
}
