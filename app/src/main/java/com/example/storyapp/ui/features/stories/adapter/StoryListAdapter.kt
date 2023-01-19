package com.example.storyapp.ui.features.stories.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.StoryItemBinding

class StoryListAdapter : PagingDataAdapter<Story, StoryListAdapter.ListViewHolder>(ITEM_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Story)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyData = getItem(position)

        with(holder.binding) {
            tvDescription.text = storyData?.description
            tvUsername.text = storyData?.name

            Glide.with(holder.itemView.context)
                .load(storyData?.photoUrl)
                .encodeQuality(75)
                .placeholder(R.drawable.baseline_image_24)
                .into(ivThumbnail)
        }

        holder.itemView.setOnClickListener {
            if (storyData != null) {
                onItemClickCallback.onItemClicked(storyData)
            }
        }
    }

    inner class ListViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val ITEM_CALLBACK: DiffUtil.ItemCallback<Story> =
            object: DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                    return oldItem == newItem
                }

            }
    }
}