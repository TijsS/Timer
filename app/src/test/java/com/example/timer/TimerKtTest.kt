package com.example.timer

import com.example.timer.feature_timer.intTimeToString
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
}