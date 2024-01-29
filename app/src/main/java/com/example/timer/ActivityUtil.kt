package com.example.timer

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context

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
