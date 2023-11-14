package com.example.timer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration.createParallel
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.timer.ui.theme.TimerTheme


class MainActivity : ComponentActivity() {

    // Get Vibrator service
    private val vibratorManager: VibratorManager by lazy {
        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO improve notification request user experience
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        when {
            ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, POST_NOTIFICATIONS) -> {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
            else -> {
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }

        setContent {
            TimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TimerScreen(
                        stopVibrate = { stopVibrate() },
                        updateNotification = { id: Int, body: String -> this.updateNotificationContentText( id, body ) },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        stopService(Intent(this, TimerService::class.java))
        super.onDestroy()
    }

    private fun stopVibrate() {
        vibratorManager.cancel()
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimerTheme {
        TimerScreen( { }, { _, _ -> } )
    }
}