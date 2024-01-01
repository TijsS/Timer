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
import com.example.weartimer.R
import com.example.weartimer.presentation.MainActivity


lateinit var notificationChannel: NotificationChannel
lateinit var notificationManager: NotificationManager

const val CHANNEL_ID = "13"
const val NOTIFICATION_ID = "13"
const val CHANNEL_NAME = "Timer Notifications"

@RequiresApi(Build.VERSION_CODES.S)
fun createNotification(context : Context): Notification {

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
    val bigTextStyle = NotificationCompat.BigTextStyle()
        .bigText(ClockTimer.timeRemaining.value.toString())


    // 4. Build and issue the notification.
    val notificationCompatBuilder =
        NotificationCompat.Builder(context, NOTIFICATION_ID)

    val notificationBuilder = notificationCompatBuilder
        .setStyle(bigTextStyle)
        .setContentTitle("titleText")
        .setContentText("mainText")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        // Makes Notification an Ongoing Notification (a Notification with a background task).
        .setOngoing(true)
        // For an Ongoing Activity, used to decide priority on the watch face.
        .setCategory(NotificationCompat.CATEGORY_WORKOUT)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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

    val ongoingActivity =
        OngoingActivity.Builder(context, NOTIFICATION_ID.toInt(), notificationBuilder)
            // Sets icon that will appear on the watch face in active mode. If it isn't set,
            // the watch face will use the static icon in active mode.
            .setAnimatedIcon(R.drawable.baseline_alarm_24)
            // Sets the icon that will appear on the watch face in ambient mode.
            // Falls back to Notification's smallIcon if not set. If neither is set,
            // an Exception is thrown.
            .setStaticIcon(R.drawable.baseline_alarm_24)
            // Sets the tap/touch event, so users can re-enter your app from the
            // other surfaces.
            // Falls back to Notification's contentIntent if not set. If neither is set,
            // an Exception is thrown.
            .setTouchIntent(activityPendingIntent)
            // In our case, sets the text used for the Ongoing Activity (more options are
            // available for timers and stop watches).
            .build()

    // Applies any Ongoing Activity updates to the notification builder.
    // This method should always be called right before you build your notification,
    // since an Ongoing Activity doesn't hold references to the context.
    ongoingActivity.apply(context)

    return notificationBuilder.build()
}

fun Context.dismissNotification(channelId: String) {
    notificationManager.cancel(channelId.toInt())
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.updateNotificationContentText(id: Int, newBody: String) {
//
//    val pauseIntent = Intent(this, TimerNotificationReceiver::class.java)
//    pauseIntent.action = TimerNotificationReceiver.Action.Pause.toString()
//
//    val pausePendingIntent = PendingIntent.getBroadcast(
//        this,
//        2,
//        pauseIntent,
//        PendingIntent.FLAG_IMMUTABLE
//    )
//
//    val resetIntent = Intent(this, TimerNotificationReceiver::class.java)
//    resetIntent.action = TimerNotificationReceiver.Action.Reset.toString()
//
//    val resetPendingIntent = PendingIntent.getBroadcast(
//        this,
//        2,
//        resetIntent,
//        PendingIntent.FLAG_IMMUTABLE
//    )
//
//    val playIntent = Intent(this, TimerNotificationReceiver::class.java)
//    playIntent.action = TimerNotificationReceiver.Action.Play.toString()
//
//    val playPendingIntent = PendingIntent.getBroadcast(
//        this,
//        2,
//        playIntent,
//        PendingIntent.FLAG_IMMUTABLE
//    )
//
//    val existingNotification = notificationManager.activeNotifications.find {
//        it.id == id
//    }
//    val notificationCompatBuilder =
//        NotificationCompat.Builder(this, NOTIFICATION_ID)
//
//    val notificationBuilder = notificationCompatBuilder
//        .setContentTitle("titleText")
//        .setContentText("mainText")
//        .setSmallIcon(R.mipmap.ic_launcher)
//        .setDefaults(NotificationCompat.DEFAULT_ALL)
//        // Makes Notification an Ongoing Notification (a Notification with a background task).
//        .setOngoing(true)
//        // For an Ongoing Activity, used to decide priority on the watch face.
//        .setCategory(NotificationCompat.CATEGORY_WORKOUT)
//        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        if (ClockTimer.timerState.value == TimerState.Paused) {
//            notificationCompatBuilder.addAction (
//                R.drawable.baseline_play_arrow_24,
//                "Play",
//                playPendingIntent
//            )
//        } else {
//            notificationCompatBuilder.addAction(
//                R.drawable.baseline_pause_24,
//                "Pause",
//                pausePendingIntent
//            )
//        }
//        notificationCompatBuilder.addAction(
//            R.drawable.baseline_autorenew_24,
//            "Reset",
//            resetPendingIntent
//        )
//
//
//    if (existingNotification != null) {
//        OngoingActivity.Builder(this, NOTIFICATION_ID.toInt(), notificationBuilder)
//            // Sets icon that will appear on the watch face in active mode. If it isn't set,
//            // the watch face will use the static icon in active mode.
//            .setAnimatedIcon(R.drawable.baseline_alarm_24)
//            // Sets the icon that will appear on the watch face in ambient mode.
//            // Falls back to Notification's smallIcon if not set. If neither is set,
//            // an Exception is thrown.
//            .setStaticIcon(R.drawable.baseline_alarm_24)
//            // Sets the tap/touch event, so users can re-enter your app from the
//            // other surfaces.
//            // Falls back to Notification's contentIntent if not set. If neither is set,
//            // an Exception is thrown.
//            .setTouchIntent(pausePendingIntent)
//            // In our case, sets the text used for the Ongoing Activity (more options are
//            // available for timers and stop watches).
//            .build()
//
//
////         Update the notification with the new content
//        notificationManager.notify(id, notificationCompatBuilder.build())
//    }
}

@RequiresApi(34)
fun Context.updateNotificationAlarmFinished(
    id: Int,
) {
    val existingNotification = notificationManager.activeNotifications.find {
        it.id == id
    }

    if (existingNotification != null) {
        dismissNotification(id.toString())
    }

    val activityIntent = Intent(this, MainActivity::class.java)

    val activityPendingIntent = PendingIntent.getActivity(
        this,
        2,
        activityIntent,
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

    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Timer")
        .setContentText("Times up!")
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
