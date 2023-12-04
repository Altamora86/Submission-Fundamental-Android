package com.dicoding.submissionawal.ui.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionawal.R
import com.dicoding.submissionawal.adapter.ListAdapter
import com.dicoding.submissionawal.database.FavoriteEntity
import com.dicoding.submissionawal.databinding.ActivityFavoriteBinding
import com.dicoding.submissionawal.response.ItemsItem
import com.dicoding.submissionawal.ui.main.DetailActivity
import android.view.View

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var listAdapter: ListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = resources.getString(R.string.app_name4)

        favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        favoriteViewModel.userList.observe(this) { users ->
            setUser(users)
        }

        listAdapter = ListAdapter(ArrayList())
        listAdapter.notifyDataSetChanged()

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvFav.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.rvFav.layoutManager = LinearLayoutManager(this)
        }

        binding.rvFav.adapter = listAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        favoriteViewModel.getFavoriteUser()?.observe(this) { users ->
            if (users != null) {
                val list = mapList(users)
                Log.d("FavoriteActivity", "Favorite Users: $list")
                listAdapter.setList(list)
                listAdapter.notifyDataSetChanged()
                if (list.isEmpty()) {
                    binding.emptyTextView.visibility = View.VISIBLE
                    binding.rvFav.visibility = View.GONE
                } else {
                    binding.emptyTextView.visibility = View.GONE
                    binding.rvFav.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun setUser(users: List<ItemsItem>?) {

        listAdapter = ListAdapter(users as ArrayList<ItemsItem>)
        binding.rvFav.adapter = listAdapter
        Log.d("TESTING-SetUser", "$users")

        listAdapter.setOnItemClickListener (object : ListAdapter.OnItemClickListener {
            override fun onItemSelect(data: ItemsItem) {
                Log.d("TESTING-OnClicked", "$data")
                showSelected(data)
            }
        })
    }
    private fun showSelected(user: ItemsItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USERNAME, user.login)
        intent.putExtra(DetailActivity.EXTRA_URL, user.avatarUrl)
        startActivity(intent)
    }
    private fun mapList(users: List<FavoriteEntity>): ArrayList<ItemsItem> {
        val listUsers = ArrayList<ItemsItem>()
        for (user in users){
            val userMapped = ItemsItem(
                user.login,
                user.avatarUrl
            )
            listUsers.add(userMapped)
        }
        Log.d("FavoriteActivity", "List Mapped: $listUsers")
        return listUsers
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}