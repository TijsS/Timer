package com.example.timer.feature_timer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timer.feature_timer.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    @Query("SELECT * FROM timer")
    fun getTimersFlow(): Flow<List<Timer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: Timer): Long

    @Update
    suspend fun update(timer: Timer)

    @Query("DELETE FROM timer where timer.id = :timerId")
    suspend fun delete(timerId: Int)
}