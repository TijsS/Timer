package com.example.timer

import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.TimerService
import com.example.timer.feature_timer.TimerState
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeoutException


@ExtendWith(MockKExtension::class)
class TimerServiceTest {
    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    @Throws(TimeoutException::class)
    fun `reset timer resets ClockTimer `() {
    // Given
        // The timer service is started
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        )
        val binder: IBinder = serviceRule.bindService(serviceIntent)
        val service = (binder as TimerService.LocalBinder).getService()

        // The state of the timer to running with 123 seconds remaining.
        ClockTimer.timerState.value = TimerState.Running
        ClockTimer.secondsRemaining.intValue = 123


        val mockCountDownTimer: CountDownTimer = mockk<CountDownTimer>()
        val serviceSpy = spyk(service)

        every { serviceSpy.createCountDownTimer() } returns mockCountDownTimer

    // When
        // The timer is reset
        Intent(
            ApplicationProvider.getApplicationContext(),
            serviceSpy::class.java
        ).also { intent ->
            intent.action = TimerService.Action.Reset.toString()
            serviceSpy.onStartCommand(intent, 0, 0)
        }

    // Then
        // The timer should be stopped with 0 seconds remaining.
        assertEquals(TimerState.Stopped, ClockTimer.timerState.value)
        assertEquals(0, ClockTimer.secondsRemaining.intValue)

        // The countDownTimer should be null
        assertEquals(null, service.countDownTimer)
    }
}