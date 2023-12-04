package com.dicoding.submissionawal.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionawal.response.ItemsItem
import com.dicoding.submissionawal.ui.main.DetailActivity
import com.dicoding.submissionawal.adapter.ListAdapter
import com.dicoding.submissionawal.databinding.FragmentProfilBinding
import com.dicoding.submissionawal.viewmodel.DetailViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFollow.layoutManager = LinearLayoutManager(activity)
        when (arguments?.getInt(ARG_SECTION_NUMBER, 0)) {
            1 -> {
                detailViewModel.getFollowers(arguments?.getString(USERNAME))
                detailViewModel.followers.observe(requireActivity()) { users ->
                    setUserData(users)
                }
                detailViewModel.isLoading.observe(viewLifecycleOwner) {showLoading(it)}
            }

            2 -> {
                detailViewModel.getFollowing(arguments?.getString(USERNAME))
                detailViewModel.following.observe(requireActivity()) { users ->
                    setUserData(users)
                }
                detailViewModel.isLoading.observe(viewLifecycleOwner) {showLoading(it)}
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setUserData(users: List<ItemsItem>?) {
        val listUserAdapter = ListAdapter(users as ArrayList<ItemsItem>)
        binding.rvFollow.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(user: ItemsItem) {
        val detailUserIntent = Intent(activity, DetailActivity::class.java)
        detailUserIntent.putExtra(DetailActivity.EXTRA_USERNAME, user.login)
        startActivity(detailUserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val USERNAME = "username"
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}