package com.example.space_timetagger.location.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.space_timetagger.location.domain.models.LatLng
import com.example.space_timetagger.location.domain.repository.LocationRepository
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationRepositoryImpl(
    private val context: Context,
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun findCurrentLocation(onResult: (LatLng?) -> Unit) {
        if (!hasFineLocationPermission(context)) {
            onResult(null)
            return
        }

        fusedLocationProviderClient.getCurrentLocation(
            CurrentLocationRequest
                .Builder()
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setMaxUpdateAgeMillis(0L)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build(),
            null,
        ).addOnSuccessListener {
            onResult(it.toLatLng())
        }.addOnFailureListener {
            onResult(null)
        }
    }

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private fun hasFineLocationPermission(context: Context) = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
}

fun Location.toLatLng() = LatLng(latitude, longitude)
