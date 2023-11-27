package com.example.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.timer.ui.theme.TimerTheme

class LockScreenActivity : ComponentActivity() {
    private val timerApp = TimerApp() // Create an instance of TimerApp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                 Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TimerScreen(
                        { timerApp.startListening( this ) }
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