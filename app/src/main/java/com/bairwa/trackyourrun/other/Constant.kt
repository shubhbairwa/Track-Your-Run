package com.bairwa.trackyourrun.other

import android.graphics.Color

object Constant {
    const val RUNNING_DATABSE_NAME = "running_db"
    const val REQUEST_CODE_LOCATION = 0
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 15f
    const val MAP_ZOOM = 18f

    const val SPLASH_TIME = 3000L
    const val SHARED_PREFERENCE_NAME = "sharedPref"

    const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"
    const val UPDATE_TIME_INTERVAL = 50L
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_UPDATE_INTERVAL = 2000L
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1


}