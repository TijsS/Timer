package com.example.timer

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.WindowManager

fun Activity.turnScreenOnAndKeyguardOff() {
    setShowWhenLocked(true)
    setTurnScreenOn(true)
    Log.d("xxx", "turnScreenOnAndKeyguardOff: turnoff keyboard")

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    setShowWhenLocked(false)
    setTurnScreenOn(false)
}
