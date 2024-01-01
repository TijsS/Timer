package com.example.timer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WAKE_LOCK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.timer.feature_timer.presentation.TimerScreen
import com.example.timer.feature_timer.TimerService
import com.example.timer.ui.theme.TimerTheme


class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissions = arrayOf(
        RECORD_AUDIO,
        POST_NOTIFICATIONS,
        WAKE_LOCK
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionsToRequest = ArrayList<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                0
            )
        } else {
            // All permissions already granted
            // Speechrecognizer might not be available on first launch/all devices
            if ( SpeechRecognizer.isRecognitionAvailable(this) ) {
                startListening()
            }
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            TimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column {
                        TimerScreen(
                            startListening = { startListening() },
                            windowSizeClass = windowSizeClass
                        )
                    }

                }
            }
        }
        turnScreenOnAndKeyguardOff()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startListening() {
        Intent(applicationContext, TimerService::class.java).also { intent ->
            intent.action = TimerService.Action.StartListening.toString()
            applicationContext.startService(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onDestroy() {

        turnScreenOffAndKeyguardOn()
        stopService(Intent(this, TimerService::class.java))
        super.onDestroy()
    }
}


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TimerTheme {
        TimerScreen({ }, windowSizeClass = WindowSizeClass.calculateFromSize(DpSize.Zero.copy(500.dp, 1000.dp)))
    }
}