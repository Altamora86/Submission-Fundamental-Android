package com.dicoding.submissionawal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite_user")
data class FavoriteEntity(
    @PrimaryKey
    val login: String,
    val avatarUrl: String
): Serializable
