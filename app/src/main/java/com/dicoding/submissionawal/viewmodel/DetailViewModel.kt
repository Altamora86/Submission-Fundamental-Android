package com.dicoding.submissionawal.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.submissionawal.database.FavoriteEntity
import com.dicoding.submissionawal.database.FavoriteDao
import com.dicoding.submissionawal.database.FavoriteRoomDatabase
import com.dicoding.submissionawal.response.ItemsItem
import com.dicoding.submissionawal.response.ResponseSearchDetail
import com.dicoding.submissionawal.retrofit.ApiConfig
import com.dicoding.submissionawal.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private var userDao: FavoriteDao?
    private var userDb: FavoriteRoomDatabase?

    init {
        userDb = FavoriteRoomDatabase.getDatabase(application)
        userDao = userDb?.favoriteUserDao()
    }
    private val _userDetail = MutableLiveData<ResponseSearchDetail>()
    val userDetail: LiveData<ResponseSearchDetail> = _userDetail

    private val _followers = MutableLiveData<List<ItemsItem>>()
    val followers: LiveData<List<ItemsItem>> = _followers

    private val _following = MutableLiveData<List<ItemsItem>>()
    val following: LiveData<List<ItemsItem>> = _following

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getFollowers(username: String?) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _followers.value = response.body()
                    _toastText.value = Event("Success")
                } else {
                    _toastText.value = Event("Tidak ada data yang ditampilkan!")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event("onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getFollowing(username: String?) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>,
                response: Response<List<ItemsItem>>
            ) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    _following.value = response.body()
                    _toastText.value = Event("Success")
                } else {
                    _toastText.value = Event("Tidak ada data yang ditampilkan!")
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event("onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(username)
        client.enqueue(object : Callback<ResponseSearchDetail> {
            override fun onResponse(
                call: Call<ResponseSearchDetail>,
                response: Response<ResponseSearchDetail>
            ) {

                if (response.isSuccessful) {
                    _isLoading.value = false
                    _userDetail.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: gagal")
                }
            }

            override fun onFailure(call: Call<ResponseSearchDetail>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }
    fun addToFavorite(username: String, avatarUrl: String){
        CoroutineScope(Dispatchers.IO).launch{
            val user = FavoriteEntity(
                username,
                avatarUrl,
            )
            userDao?.addAllFavorite(user)
        }
    }

    fun checkUser(login: String, avatarUrl: String)= userDao?.userCheck(login, avatarUrl)

    fun removeFromFavorite(login: String, avatarUrl: String){
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.removeFavorite(login, avatarUrl)
        }
    }
    companion object {
        private const val TAG = "DetailViewModel"
    }


}