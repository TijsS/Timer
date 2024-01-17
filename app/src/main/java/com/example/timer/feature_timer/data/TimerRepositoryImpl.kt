package com.example.timer.feature_timer.data

import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow

class TimerRepositoryImpl(
    private val timerDao: TimerDao
    ) : TimerRepository {

    override fun getTimersFlow(): Flow<List<Timer>> {
        return timerDao.getTimersFlow()
    }

    override suspend fun addTimer(timer: Timer) {
        timerDao.insert(timer)
    }

    override suspend fun updateTimer(timer: Timer) {
        timerDao.update(timer)
    }

    override suspend fun removeTimer(timerId: Int) {
        timerDao.delete(timerId)
    }
}