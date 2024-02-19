package com.example.timer

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.test.mock.MockContext
import androidx.annotation.MainThread
import androidx.core.app.NotificationManagerCompat
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ServiceTestRule
import com.example.timer.feature_notification.NOTIFICATION_ID
import com.example.timer.feature_notification.createNotification
import com.example.timer.feature_notification.notificationManager
import com.example.timer.feature_notification.updateNotificationContentText
import com.example.timer.feature_timer.ClockTimer
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.TimerService
import com.example.timer.feature_timer.TimerState
import com.example.timer.feature_timer.intTimeToString
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.TimeoutException
import kotlin.coroutines.coroutineContext


@ExtendWith(MockKExtension::class)
class TimerServiceTest {
    @get:Rule
    val serviceRule = ServiceTestRule()

    private lateinit var serviceSpy: TimerService
    private lateinit var mockCountDownTimer: CountDownTimer

    @Before
    fun setup() {
        // The timer service is started
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        )
        val binder: IBinder = serviceRule.bindService(serviceIntent)
        val service = (binder as TimerService.LocalBinder).getService()

        // The TimerService gets a spy to prevent a CountDownTimer from being created
        serviceSpy = spyk(service)
        mockCountDownTimer = mockk<CountDownTimer>()

        every { serviceSpy.createCountDownTimer().start() } returns mockCountDownTimer
        justRun { serviceSpy.startListeningSafe() }
        justRun { mockCountDownTimer.cancel() }
    }

    @After
    fun tearDown() {

    }

    @Test
    @MainThread
    @Throws(TimeoutException::class)
    fun `reset timer resets ClockTimer`() {
    // Given

        // The state of the timer is set to 123 seconds remaining.
        ClockTimer.secondsRemaining.intValue = 123

        // The Timer gets started
        Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        ).also { intent ->
            intent.action = TimerService.Action.NotifiedStart.toString()
            serviceSpy.onStartCommand(intent, 0, 0)
        }

    // When
        // The timer is reset
        Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        ).also { intent ->
            intent.action = TimerService.Action.Reset.toString()
            serviceSpy.onStartCommand(intent, 0, 0)
        }

    // Then
        // The timer should be stopped with 0 seconds remaining.
        assertEquals(TimerState.Stopped, ClockTimer.timerState.value)
        assertEquals(0, ClockTimer.secondsRemaining.intValue)

        // verify that the CountDownTimer got canceled
        verify { mockCountDownTimer.cancel() }
    }

    @Test
    @MainThread
    fun `start starts ClockTimer`() {
    // Given


    // When
        // The timer gets started
        Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        ).also { intent ->
            intent.action = TimerService.Action.NotifiedStart.toString()
            serviceSpy.onStartCommand(intent, 0, 0)
        }

    // Then
        // Confirm that ClockTimer is set to running
        assertEquals(ClockTimer.timerState.value, TimerState.Running)

        // Verify that the old countDownTimer is cancelled and the new one has been called
        verify { serviceSpy.createCountDownTimer().start() }
    }

    @Test
    fun `pause pauses ClockTimer`() {
    // Given

    // When
        // The timer gets paused
        Intent(
            ApplicationProvider.getApplicationContext(),
            TimerService::class.java
        ).also { intent ->
            intent.action = TimerService.Action.NotifiedPause.toString()
            serviceSpy.onStartCommand(intent, 0, 0)
        }

    // Then
        // Confirm that ClockTimer is set to paused
        assertEquals(ClockTimer.timerState.value, TimerState.Paused)
    }
}