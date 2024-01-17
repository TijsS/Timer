package com.example.timer

import android.app.Application
import androidx.room.Room
import com.example.timer.feature_timer.data.TimerRepository
import com.example.timer.feature_timer.data.TimerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimerModule {

    @Provides
    @Singleton
    fun provideTimerDatabase(app: Application): TimerDatabase {
        return Room.databaseBuilder(
            app,
            TimerDatabase::class.java,
            TimerDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }



    //Repostitory's
    @Provides
    @Singleton
    fun provideDeckRepository(db: TimerDatabase): TimerRepository {
        return TimerRepositoryImpl(db.timerDao)
    }
}
