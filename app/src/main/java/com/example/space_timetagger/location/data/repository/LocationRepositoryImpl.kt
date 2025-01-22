package com.example.space_timetagger.location.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.example.space_timetagger.location.domain.hasFineLocationPermission
import com.example.space_timetagger.location.domain.models.LatLng
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationRepositoryImpl(
    private val context: Context,
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun findCurrentLocation(): LatLng? {
        if (!context.hasFineLocationPermission()) {
            return null
        }

        try {
            val currentLocation = fusedLocationProviderClient.getCurrentLocation(
                CurrentLocationRequest
                    .Builder()
                    .setGranularity(Granularity.GRANULARITY_FINE)
                    .setMaxUpdateAgeMillis(0L)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build(),
                null,
            ).await()
            return currentLocation.toLatLng()
        } catch (e: Exception) {
            Log.e("LocationRepositoryImpl", e.toString())
            return null
        }
    }

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}

fun Location.toLatLng() = LatLng(latitude, longitude)
