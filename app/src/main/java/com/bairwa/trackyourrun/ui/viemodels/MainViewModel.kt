package com.bairwa.trackyourrun.ui.viemodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.bairwa.trackyourrun.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) :ViewModel(){
}