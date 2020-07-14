package com.bairwa.trackyourrun.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bairwa.trackyourrun.db.RunningDatabase
import com.bairwa.trackyourrun.other.Constant.RUNNING_DATABSE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
//With its help, it will remain until the application's lifecycle
@InstallIn(ApplicationComponent::class)
object AppModule  {
@Singleton
@Provides
    fun provideRunningDatabase(
        @ApplicationContext app:Context
    )= Room.databaseBuilder(
        app,RunningDatabase::class.java,RUNNING_DATABSE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db:RunningDatabase)=db.getRunDao()
}