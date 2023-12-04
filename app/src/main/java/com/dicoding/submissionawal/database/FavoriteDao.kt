package com.dicoding.submissionawal.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert
    fun addAllFavorite(favoritUser: FavoriteEntity)

    @Query("SELECT * FROM favorite_user")
    fun getAllFavorite(): LiveData<List<FavoriteEntity>>

    @Query("SELECT count(*) FROM favorite_user WHERE login = :login AND avatarUrl = :avatarUrl")
    fun userCheck(login: String, avatarUrl: String): Int

    @Query("DELETE FROM favorite_user WHERE login = :login AND avatarUrl = :avatarUrl")
    fun removeFavorite(login: String, avatarUrl: String): Int
}
