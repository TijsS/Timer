package com.example.timer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.timer.feature_timer.Timer
import com.example.timer.feature_timer.data.TimerDao

@Database(entities = [Timer::class], version = 1, exportSchema = false)
abstract class TimerDatabase : RoomDatabase() {
    abstract val timerDao: TimerDao

    companion object {
        const val DATABASE_NAME = "timer_db"
    }
}