package com.example.timer

import android.os.CountDownTimer
import com.example.timer.feature_timer.intTimeToString
import com.example.timer.feature_timer.toHours
import com.example.timer.feature_timer.toMinutes
import com.example.timer.feature_timer.toSeconds
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test


class TimerKtTest {

    @Test
    fun intTimeToStringTest() {
        // Test cases for the intTimeToString extension function
        assertEquals("00:00", 0.intTimeToString())
        assertEquals("00:01", 1.intTimeToString())
        assertEquals("00:59", 59.intTimeToString())
        assertEquals("01:00", 60.intTimeToString())
        assertEquals("1:00:00", 3600.intTimeToString())
        assertEquals("10:00:00", 36000.intTimeToString())
    }

    @Test
    fun toHoursTest() {
        // Test cases for the toHours extension function
        assertEquals(0, 0.toHours())
        assertEquals(0, 3599.toHours())
        assertEquals(1, 3600.toHours())
        assertEquals(10, 36000.toHours())
    }

    @Test
    fun toMinutesTest() {
        // Test cases for the toMinutes extension function
        assertEquals(0, 0.toMinutes())
        assertEquals(0, 59.toMinutes())
        assertEquals(1, 60.toMinutes())
        assertEquals(0, 3600.toMinutes())
        assertEquals(0, 36000.toMinutes())
    }

    @Test
    fun toSecondsTest() {
        // Test cases for the toSeconds extension function
        assertEquals(0, 0.toSeconds())
        assertEquals(59, 59.toSeconds())
        assertEquals(0, 60.toSeconds())
        assertEquals(0, 3600.toSeconds())
        assertEquals(0, 36000.toSeconds())
    }
}