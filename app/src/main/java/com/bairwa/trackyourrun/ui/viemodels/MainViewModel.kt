package com.bairwa.trackyourrun.ui.viemodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bairwa.trackyourrun.db.Run
import com.bairwa.trackyourrun.repositories.MainRepository
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) :ViewModel(){
val runSortedByDate=mainRepository.getAllRunSortedByDate()
    fun insertRun(run:Run)=viewModelScope.launch {
        mainRepository.insertRun(run)
    }

}