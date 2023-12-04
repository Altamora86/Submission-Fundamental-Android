package com.dicoding.submissionawal.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.submissionawal.fragment.HomeFragment

class SectionPagerAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity){
    var username: String = ""

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = HomeFragment()
        fragment.arguments = Bundle().apply {
            putInt(HomeFragment.ARG_SECTION_NUMBER, position + 1)
            putString(HomeFragment.USERNAME, username)
        }
        return fragment
    }
}