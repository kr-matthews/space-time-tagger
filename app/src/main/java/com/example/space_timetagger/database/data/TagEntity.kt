package com.example.space_timetagger.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.example.space_timetagger.location.domain.models.LatLng
import com.example.space_timetagger.sessions.domain.models.Tag
import java.time.LocalDateTime

@Entity(
    tableName = "tags",
    foreignKeys = [ForeignKey(
        entity = SessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = CASCADE,
    )]
)
data class TagEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(index = true) val sessionId: String,
    @ColumnInfo(name = "date_time") val dateTime: String,
    val lat: Double?,
    val long: Double?,
)

fun Tag.toEntity(sessionId: String) = TagEntity(
    id = id,
    sessionId = sessionId,
    dateTime = dateTime.toString(),
    lat = location?.latitude,
    long = location?.longitude,
)

fun TagEntity.toTag() = Tag(
    id = id,
    dateTime = LocalDateTime.parse(dateTime),
    location = lat?.let { long?.let { LatLng(lat, long) } },
)
