package com.bairwa.trackyourrun.other

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtiltiy {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun getFormattedStopWatchTIme(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        var hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        var minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        var seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:"+
                    "${if (minutes<10)"0" else ""}$minutes:"+
                    "${if (seconds<10) "0" else ""}$seconds"
        }

        milliseconds-=TimeUnit.SECONDS.toMillis(seconds)
        milliseconds/=10
        return "${if (hours < 10) "0" else ""}$hours:"+
                "${if (minutes<10)"0" else ""}$minutes:"+
                "${if (seconds<10) "0" else ""}$seconds:"+
                "${if (milliseconds<10) "0" else ""}$milliseconds"
    }



}