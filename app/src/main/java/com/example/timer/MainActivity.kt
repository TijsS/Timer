package com.example.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.CombinedVibration.createParallel
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ServiceCompat
import com.example.timer.ui.theme.TimerTheme


class MainActivity : ComponentActivity() {

    // Get Vibrator service
    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        startService(Intent(this, TimerService::class.java))

        setContent {
            TimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TimerScreen(
                        vibrate = { vibrate() },
                        stopVibrate = { stopVibrate() },
                        notify = { id: String, title: String, body: String -> this.showNotification( id, title, body ) },
                        updateNotification = { id: String, title: String, body: String -> this.updateNotificationContentText( id, title, body ) },
                        dismissNotification = { this.dismissNotification("12") }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, TimerService::class.java))
        super.onDestroy()
    }

    private fun vibrate() {
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimerTheme {
        TimerScreen( { }, { }, { _, _, _ -> }, { _, _, _ -> }, { } )
    }
}