package com.example.foodapp

import android.app.Application
import com.example.foodapp.nofication.FoodAppNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodApp : Application() {

    @Inject
    lateinit var foodAppNotificationManager: FoodAppNotificationManager
    override fun onCreate() {
        super.onCreate()
        foodAppNotificationManager.createChannels()
        foodAppNotificationManager.getAndStoreToken()
    }
}