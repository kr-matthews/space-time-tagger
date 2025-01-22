package com.example.space_timetagger.location.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Context.hasFineLocationPermission() = ActivityCompat.checkSelfPermission(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION,
) == PackageManager.PERMISSION_GRANTED
