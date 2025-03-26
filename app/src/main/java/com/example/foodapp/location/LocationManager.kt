package com.example.foodapp.location

import android.annotation.SuppressLint
import android.content.ContentProviderClient
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(val fusedLocationProviderClient: FusedLocationProviderClient) {
    @SuppressLint("MissingPermission")
    fun getLocation(): Flow<Location> = flow {
        val location = fusedLocationProviderClient.lastLocation.await()
        emit(location)
    }.flowOn(Dispatchers.IO)
}