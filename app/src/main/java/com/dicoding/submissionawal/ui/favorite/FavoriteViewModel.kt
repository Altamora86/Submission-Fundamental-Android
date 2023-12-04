package com.dicoding.submissionawal.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.submissionawal.database.FavoriteEntity
import com.dicoding.submissionawal.database.FavoriteDao
import com.dicoding.submissionawal.database.FavoriteRoomDatabase
import com.dicoding.submissionawal.response.ItemsItem

class FavoriteViewModel(application: Application): AndroidViewModel(application) {
    private var userDao: FavoriteDao?
    private var userDatabase: FavoriteRoomDatabase?
    private val _userList = MutableLiveData<List<ItemsItem>>()
    val userList: LiveData<List<ItemsItem>> = _userList
    init {
        userDatabase = FavoriteRoomDatabase.getDatabase(application)
        userDao = userDatabase?.favoriteUserDao()
    }

    fun getFavoriteUser(): LiveData<List<FavoriteEntity>>? {
        return userDao?.getAllFavorite()
    }
}