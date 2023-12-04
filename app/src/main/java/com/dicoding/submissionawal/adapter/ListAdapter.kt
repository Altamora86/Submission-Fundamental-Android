package com.dicoding.submissionawal.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submissionawal.databinding.ItemReviewBinding
import com.dicoding.submissionawal.response.ItemsItem
import com.dicoding.submissionawal.ui.main.DetailActivity

class ListAdapter(private val listUser: ArrayList<ItemsItem>) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
    inner class ListViewHolder(var binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        holder.binding.tvItemUsername.text = user.login
        Glide.with(holder.binding.imgItemPhoto.context)
            .load(user.avatarUrl)
            .circleCrop()
            .into(holder.binding.imgItemPhoto)

        holder.itemView.setOnClickListener {
            val selectedUser = listUser[holder.bindingAdapterPosition]
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_USERNAME, selectedUser.login)
            intent.putExtra(DetailActivity.EXTRA_URL, selectedUser.avatarUrl)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listUser.size

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemsItem)
    }
    interface OnItemClickListener {
        fun onItemSelect(data: ItemsItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(itemsItem: ArrayList<ItemsItem>) {
        listUser.clear()
        listUser.addAll(itemsItem)
        notifyDataSetChanged()
    }
}
