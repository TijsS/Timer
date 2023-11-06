package com.example.timer

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class TimerKtTest {

    @Test
    fun intTimeToStringTest() {
        // Test cases for the intTimeToString extension function
        assertEquals("00:00", 0.intTimeToString())
        assertEquals("00:01", 1.intTimeToString())
        assertEquals("00:59", 59.intTimeToString())
        assertEquals("01:00", 60.intTimeToString())
        assertEquals("01:01", 61.intTimeToString())
        assertEquals("01:59", 119.intTimeToString())
        assertEquals("10:00", 36000.intTimeToString())
        assertEquals("10:01", 36001.intTimeToString())
        assertEquals("10:59", 36059.intTimeToString())
        assertEquals("11:00", 39600.intTimeToString())
        assertEquals("11:01", 39601.intTimeToString())
        assertEquals("11:59", 39659.intTimeToString())
        assertEquals("99:59", 359999.intTimeToString())
    }
}