package com.example.timer

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.example.timer.feature_timer.TimerService
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class TimerServiceTest {
    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        // Create the service Intent.
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            TimerService::class.java
        ).apply {
            // Data can be passed to the service via the Intent.
            putExtra(SEED_KEY, 42L)
        }

        // Bind the service and grab a reference to the binder.
        val binder: IBinder = serviceRule.bindService(serviceIntent)

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        val service = ((TimerService()).onBind(serviceIntent) )


        // Verify that the service is working correctly.
        assertThat(service.getRandomInt(), `is`(any(Int::class.java)))
    }

}