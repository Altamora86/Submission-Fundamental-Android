package com.dicoding.submissionawal.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionawal.R
import com.dicoding.submissionawal.response.ItemsItem
import com.dicoding.submissionawal.adapter.ListAdapter
import com.dicoding.submissionawal.databinding.ActivityMainBinding
import com.dicoding.submissionawal.ui.favorite.FavoriteActivity
import com.dicoding.submissionawal.ui.setting.SettingActivity
import com.dicoding.submissionawal.ui.setting.SettingPreferences
import com.dicoding.submissionawal.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        darkModeCheck()
        loading(false)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvResult.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvResult.layoutManager = LinearLayoutManager(this)
        }

        mainViewModel.userList.observe(this) { users ->
            setUser(users)
            loading(false)
        }
        mainViewModel.userCount.observe(this){
            binding.tvResult.text = resources.getString(R.string.tv_data_result, it)
        }
        mainViewModel.isLoading.observe(this) {
            loading(it)
        }
        mainViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                loading(true)
                mainViewModel.findUser(query)
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    loading(false)
                }
                return false
            }
        })
        return true
    }
    private fun setUser(users: List<ItemsItem>?) {
        val userAdapter = ListAdapter(users as ArrayList<ItemsItem>)
        binding.rvResult.adapter = userAdapter

        userAdapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showSelected(data)
            }
        })
    }
    private fun loading(isLoading: Boolean) {
        binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSelected(user: ItemsItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USERNAME, user.login)
        intent.putExtra(DetailActivity.EXTRA_URL, user.avatarUrl)
        startActivity(intent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_setting -> {
                val setting = Intent(this, SettingActivity::class.java)
                startActivity(setting)
            }
            R.id.btn_favorite ->{
                val favorite = Intent(this, FavoriteActivity::class.java)
                startActivity(favorite)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setViewModel(){
        val pref = SettingPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(pref))[MainViewModel::class.java]
    }
    private fun darkModeCheck(){
        mainViewModel.getThemeSettings().observe(this@MainActivity) { isDarkModeActive ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}