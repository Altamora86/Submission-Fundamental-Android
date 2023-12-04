package com.dicoding.submissionawal.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.bumptech.glide.Glide
import com.dicoding.submissionawal.R
import com.google.android.material.tabs.TabLayoutMediator
import com.dicoding.submissionawal.databinding.ActivityDetailBinding
import com.dicoding.submissionawal.response.ResponseSearchDetail
import com.dicoding.submissionawal.adapter.SectionPagerAdapter
import com.dicoding.submissionawal.viewmodel.DetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = resources.getString(R.string.app_name2)

        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""
        val avatarUrl = intent.getStringExtra(EXTRA_URL) ?: ""
        Log.d("DetailActivity", "Username: $username, Avatar URL: $avatarUrl")

        detailViewModel.getUser(username)

        val sectionsPagerAdapter = SectionPagerAdapter(this)
        sectionsPagerAdapter.username = username

        binding.viewPager.adapter = sectionsPagerAdapter
        supportActionBar?.elevation = 0f

        detailViewModel.userDetail.observe(this) { user ->
            setUserData(user)
        }
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        detailViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var _isChecked = false

        CoroutineScope(Dispatchers.IO).launch {
            val count = detailViewModel.checkUser(username, avatarUrl)
            withContext(Dispatchers.Main) {
                if (count != null) {
                    if (count > 0) {
                        binding.toggleFavorite.isChecked = true
                        _isChecked = true
                    } else {
                        binding.toggleFavorite.isChecked = false
                        _isChecked = false
                    }
                }
            }
        }

        binding.toggleFavorite.setOnClickListener {
            _isChecked = !_isChecked
            if (_isChecked) {
                detailViewModel.addToFavorite(username, avatarUrl)
            } else {
                detailViewModel.removeFromFavorite(username, avatarUrl)
            }
            binding.toggleFavorite.isChecked = _isChecked
        }
    }

    private fun setUserData(user: ResponseSearchDetail) {
        binding.apply {
            tvName.text = user.name
            tvUsername.text = user.login
            tvFollowers.text = String.format(getString(R.string.followersString), user.followers)
            tvFollowing.text = String.format(getString(R.string.followingString), user.following)
            tvRepository.text = String.format(getString(R.string.publicrepos), user.publicRepos)
        }

        Glide.with(this)
            .load(user.avatarUrl)
            .circleCrop()
            .into(binding.imgAvatar)

        val countFollow = arrayOf(user.followers, user.following)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position], countFollow[position])
        }.attach()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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

    companion object {
        const val EXTRA_USERNAME = "extra_user"
        const val EXTRA_URL = "extra_avatarUrl"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }
}
