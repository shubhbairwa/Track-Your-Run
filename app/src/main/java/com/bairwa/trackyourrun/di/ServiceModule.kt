package com.bairwa.trackyourrun.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.bairwa.trackyourrun.R
import com.bairwa.trackyourrun.other.Constant
import com.bairwa.trackyourrun.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @ServiceScoped   //like Singleton
    @Provides
    fun getFusedLocationProviderClient(
        @ApplicationContext app:Context
    )=  FusedLocationProviderClient(app)



    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app:Context
    )= PendingIntent
        .getActivity(
            app, 0,
            Intent(app, MainActivity::class.java).also {
                it.action = Constant.ACTION_SHOW_TRACKING_FRAGMENT
            }
            , PendingIntent.FLAG_UPDATE_CURRENT
        ) //it update if it is already present in main activity

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app:Context,
        pendingIntent: PendingIntent
    )= NotificationCompat.Builder(app, Constant.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)   //cannot be cancel by clicking
        .setOngoing(true)      //no swiping allowed
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

}