package com.example.timer

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import com.example.timer.feature_timer.presentation.TimerScreen
import com.example.timer.feature_timer.TimerService
import com.example.timer.ui.theme.TimerTheme

class LockScreenActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

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
                        },
                        windowSizeClass = windowSizeClass
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

fun Activity.turnScreenOnAndKeyguardOff() {
    setShowWhenLocked(true)
    setTurnScreenOn(true)

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    setShowWhenLocked(false)
    setTurnScreenOn(false)
}
