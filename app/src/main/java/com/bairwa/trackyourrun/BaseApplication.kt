package com.bairwa.trackyourrun

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        //because we want it to stay for the whole application lifecycle
    Timber.plant(Timber.DebugTree())

    }


}