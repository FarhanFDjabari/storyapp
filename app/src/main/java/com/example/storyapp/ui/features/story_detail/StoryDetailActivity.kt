package com.example.storyapp.ui.features.story_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.ui.features.story_detail.viewModel.StoryDetailViewModel
import com.google.android.material.snackbar.Snackbar

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var detailViewModel: StoryDetailViewModel
    private var storyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Story Detail"

        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val factory = ViewModelFactory.getInstance(application)
        storyId = intent.getStringExtra("story_id")

        detailViewModel = ViewModelProvider(this, factory)[StoryDetailViewModel::class.java]

        detailViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        detailViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }

        detailViewModel.storyData.observe(this) {
            setData(it)
        }

        storyId?.let { detailViewModel.getStoryDetail(it) }
    }

    private fun setData(data: Story) {
        with(binding) {
            tvName.text = data.name
            tvDescription.text = data.description
            Glide.with(this@StoryDetailActivity)
                .load(data.photoUrl)
                .placeholder(R.drawable.baseline_image_24)
                .into(ivHeaderImage)
        }
    }

    private fun loadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.cpiLoading.visibility = View.VISIBLE
            binding.cvDetailCard.visibility = View.INVISIBLE
        } else {
            binding.cpiLoading.visibility = View.GONE
            binding.cvDetailCard.visibility = View.VISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}