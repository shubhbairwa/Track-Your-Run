package com.bairwa.trackyourrun.repositories

import com.bairwa.trackyourrun.db.Run
import com.bairwa.trackyourrun.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insertRun(run:Run)=runDao.InsertRun(run)
    suspend fun deleteRun(run:Run)=runDao.deleteRun(run)

    fun getAllRunSortedByDate()=runDao.getAllRunBySortedByDate()
    fun getAllRunSortedByDistance()=runDao.getAllRunBySortedByDistanceInMeters()
    fun getAllRunSortedByTimeInMillis()=runDao.getAllRunBySortedByTimeInMillis()
    fun getAllRunSortedByAvgSpeed()=runDao.getAllRunBySortedByAvgSpeed()
    fun getAllRunSortedByCaloriesBurned()=runDao.getAllRunBySortedByCaloriesBurned()

    fun getAllTimeInMillis()=runDao.getAllTimeInMillis()
    fun getAllDistance()=runDao.getAllDistance()
    fun getAllCaloriesBurned()=runDao.getAllCaloriesBurned()
    fun getTotalAvgSpeed()=runDao.getAverageSpeed()

}