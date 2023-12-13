package com.example.timer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.timer.ui.theme.TimerTheme

class LockScreenActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                 Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TimerScreen(
                        {
                            Intent(applicationContext, TimerService::class.java).also { intent ->
                                intent.action = TimerService.Action.StartListening.toString()
                                applicationContext.startService(intent)
                            }
                        }
                    )
                 }
            }
        }
        turnScreenOnAndKeyguardOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }
}