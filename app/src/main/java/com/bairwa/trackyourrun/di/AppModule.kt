package com.bairwa.trackyourrun.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bairwa.trackyourrun.db.RunningDatabase
import com.bairwa.trackyourrun.other.Constant.KEY_FIRST_TIME_TOGGLE
import com.bairwa.trackyourrun.other.Constant.KEY_NAME
import com.bairwa.trackyourrun.other.Constant.KEY_WEIGHT
import com.bairwa.trackyourrun.other.Constant.RUNNING_DATABSE_NAME
import com.bairwa.trackyourrun.other.Constant.SHARED_PREFERENCE_NAME
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

    @Singleton
    @Provides
    fun provideSharedPrefernces(@ApplicationContext app:Context)=
        app.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE)


    @Singleton
    @Provides
    fun provideName(sharedPref:SharedPreferences)=sharedPref.getString(KEY_NAME,"")?:""

    @Singleton
    @Provides
    fun provideWeight(sharedPref:SharedPreferences)=sharedPref.getFloat(KEY_WEIGHT,65f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref:SharedPreferences)=sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE,true)


}