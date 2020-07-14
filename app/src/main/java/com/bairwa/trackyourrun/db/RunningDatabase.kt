package com.bairwa.trackyourrun.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Run::class],
version = 1)
@TypeConverters(Convertors::class)
abstract class RunningDatabase:RoomDatabase(){
abstract fun getRunDao():RunDao
    //don't make singleton of this because Dagger will handle it

}