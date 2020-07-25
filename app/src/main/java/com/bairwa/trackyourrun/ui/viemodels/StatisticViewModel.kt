package com.bairwa.trackyourrun.ui.viemodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bairwa.trackyourrun.repositories.MainRepository

class StatisticViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) :ViewModel(){

    val totalDistance=mainRepository.getAllDistance()
    val totalTimeRun=mainRepository.getAllTimeInMillis()
    val totalCaloriesBurned=mainRepository.getAllCaloriesBurned()
    val totalAvgSpeed=mainRepository.getTotalAvgSpeed()
    val runSortedByDate=mainRepository.getAllRunSortedByDate()



}