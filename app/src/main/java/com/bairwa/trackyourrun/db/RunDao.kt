package com.bairwa.trackyourrun.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertRun(run:Run)

    @Delete
    suspend fun deleteRun(run:Run)

    @Query("SELECT * FROM running_table ORDER BY timeStamp DESC")
    fun getAllRunBySortedByDate():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunBySortedByTimeInMillis():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKmh DESC")
    fun getAllRunBySortedByAvgSpeed():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunBySortedByCaloriesBurned():LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunBySortedByDistanceInMeters():LiveData<List<Run>>

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getAllTimeInMillis():LiveData<Long>

    @Query("SELECT AVG(avgSpeedInKmh) FROM running_table")
    fun getAverageSpeed():LiveData<Float>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getAllDistance():LiveData<Int>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getAllCaloriesBurned():LiveData<Int>

}