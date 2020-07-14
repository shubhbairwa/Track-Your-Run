package com.bairwa.trackyourrun.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.other.Constant.ACTION_PAUSE_SERVICE
import com.bairwa.trackyourrun.other.Constant.ACTION_SHOW_TRACKING_FRAGMENT
import com.bairwa.trackyourrun.other.Constant.ACTION_START_OR_RESUME_SERVICE
import com.bairwa.trackyourrun.other.Constant.ACTION_STOP_SERVICE
import com.bairwa.trackyourrun.other.Constant.FASTEST_UPDATE_INTERVAL
import com.bairwa.trackyourrun.other.Constant.LOCATION_UPDATE_INTERVAL
import com.bairwa.trackyourrun.other.Constant.NOTIFICATION_CHANNEL_ID
import com.bairwa.trackyourrun.other.Constant.NOTIFICATION_CHANNEL_NAME
import com.bairwa.trackyourrun.other.Constant.NOTIFICATION_ID
import com.bairwa.trackyourrun.other.Constant.UPDATE_TIME_INTERVAL
import com.bairwa.trackyourrun.other.TrackingUtiltiy
import com.bairwa.trackyourrun.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
    var serviceKilled=false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder


    private val timeRunInSeconds = MutableLiveData<Long>()  //for notifications

    companion object {
        val timeRunInMillis = MutableLiveData<Long>() //for tracking fragment
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<polylines>()
    }

    private fun postInitialValue() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        timeRunInSeconds.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()

        currentNotificationBuilder = baseNotificationBuilder

        postInitialValue()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })

    }

    private fun killService(){
serviceKilled=true
        isFirstRun=true
        pauseService()
        postInitialValue()
        stopForeground(true)
        stopSelf()


    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("service  resume")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("service pause")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("service stop")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimeEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSeconTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolylines()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimeEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {

                //time difference b/w now and time started
                lapTime = System.currentTimeMillis() - timeStarted

                //post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSeconTimeStamp + 1000L) {//it will reformat the time
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSeconTimeStamp += 1000L
                }
                delay(UPDATE_TIME_INTERVAL)
            }
            timeRun += lapTime
        }

    }


    private fun pauseService() {
        isTracking.postValue(
            false
        )
        isTimeEnabled = false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)

        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
      if (!serviceKilled){
          currentNotificationBuilder = baseNotificationBuilder.addAction(
              R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent
          )
          notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
      }

    }


    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtiltiy.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoints(location)
                        Timber.d("NEW LOCATION ${location.latitude},${location.longitude}")

                    }
                }
            }
        }
    }

    private fun addPathPoints(location: Location) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)  //adding coordinates to the last of polyline
                pathPoints.postValue(this)

            }

        }
    }

    private fun addEmptyPolylines() = pathPoints.value?.apply {

        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        startTimer()

        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        timeRunInSeconds.observe(this, Observer {

            if(!serviceKilled){
                val notification = currentNotificationBuilder
                    .setContentText(TrackingUtiltiy.getFormattedStopWatchTIme(it * 1000L))

                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }

        })

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)


    }


}