package com.example.space_timetagger.location.domain.repository

import com.example.space_timetagger.location.domain.models.LatLng

interface LocationRepository {
    suspend fun findCurrentLocation(onResult: (LatLng?) -> Unit)
}