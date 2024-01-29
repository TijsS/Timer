package com.example.timer.feature_timer.data

import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    fun getTimersFlow(): Flow<List<Timer>>

    suspend fun addTimer(timer: Timer)

    suspend fun updateTimer(timer: Timer)

    suspend fun removeTimer(timerId: Int)

}