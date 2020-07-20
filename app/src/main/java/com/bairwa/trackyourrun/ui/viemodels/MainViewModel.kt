package com.bairwa.trackyourrun.ui.viemodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bairwa.trackyourrun.db.Run
import com.bairwa.trackyourrun.other.SortingType
import com.bairwa.trackyourrun.repositories.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) :ViewModel(){
private val runSortedByDate=mainRepository.getAllRunSortedByDate()
private val runSortedByDistance=mainRepository.getAllRunSortedByDistance()
 private val runSortedByAvgSpeed=mainRepository.getAllRunSortedByAvgSpeed()
private val runSortedByTimeInMillis=mainRepository.getAllRunSortedByTimeInMillis()
private val runSortedByCalorieBurned=mainRepository.getAllRunSortedByCaloriesBurned()


    val runs=MediatorLiveData<List<Run>>()
var sortType=SortingType.DATE

    init {
        runs.addSource(runSortedByDate){result->
            if (sortType==SortingType.DATE) {
                result?.let {
                    runs.value = it

                }
            }

        }
        runs.addSource(runSortedByAvgSpeed){ result->
            if (sortType==SortingType.AVG_SPEED) {
                result?.let {
                    runs.value = it

                }
            }

        }
        runs.addSource(runSortedByTimeInMillis){result->
            if (sortType==SortingType.RUNNING_TIME) {
                result?.let {
                    runs.value = it

                }
            }

        }
        runs.addSource(runSortedByCalorieBurned){result->
            if (sortType==SortingType.CALORIES_BURNED) {
                result?.let {
                    runs.value = it

                }
            }

        }

    }

fun sortRuns(sortType: SortingType)=when(sortType){
    SortingType.DATE->runSortedByDate.value?.let { runs.value=it }
    SortingType.AVG_SPEED->runSortedByAvgSpeed.value?.let { runs.value=it }
    SortingType.CALORIES_BURNED->runSortedByCalorieBurned.value?.let { runs.value=it }
    SortingType.RUNNING_TIME->runSortedByTimeInMillis.value?.let { runs.value=it }
    SortingType.DISTANCE->runSortedByDistance.value?.let { runs.value=it }
}.also {
    this.sortType=sortType
}

    fun insertRun(run:Run)=viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}