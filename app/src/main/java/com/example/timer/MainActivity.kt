package com.example.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration.createParallel
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import com.example.timer.ui.theme.TimerTheme

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity() {

    // Get Vibrator service
    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    var notificationBuilder = NotificationCompat.Builder(this, R.string.CHANNEL_ID )
        .setSmallIcon(R.drawable.baseline_alarm_24)
        .setContentTitle(getString(R.string.notification_content_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)


    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val id = getString(R.string.CHANNEL_ID)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TimerScreen( vibrate = { vibrate() }, stopVibrate = { stopVibrate() })
                }
            }
        }
    }

    private fun vibrate(duration: Long = 500) {
        vibratorManager.vibrate(
            createParallel(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 200, 600, 500),
                    intArrayOf(0, 255, 55, 0),
                    1
                )
            )
        )
    }

    private fun stopVibrate() {
        vibratorManager.cancel()
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimerTheme {
        TimerScreen( { }, { } )
    }
}