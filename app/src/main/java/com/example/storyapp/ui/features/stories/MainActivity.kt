package com.example.storyapp.ui.features.stories

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.ui.features.login.LoginActivity
import com.example.storyapp.ui.features.new_story.NewStoryActivity
import com.example.storyapp.ui.features.stories.adapter.StoryListAdapter
import com.example.storyapp.ui.features.stories.viewModel.MainViewModel
import com.example.storyapp.ui.features.story_detail.StoryDetailActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyListAdapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(application)

        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupRecyclerView()

        mainViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        mainViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }

        mainViewModel.stories.observe(this) {
            setStoryList(it)
        }

        mainViewModel.logout.observe(this) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        mainViewModel.getStories()

        val newStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val isNewStoryUploaded = it.data?.getBooleanExtra("is_new_story_uploaded", false)
                Log.d("MainActivity", "newStoryLauncher: $isNewStoryUploaded")
                if (isNewStoryUploaded == true) {
                    mainViewModel.getStories()
                }
            }
        }

        binding.fabAddStory.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(it, "image"),
                )
            val addStoryIntent = Intent(this, NewStoryActivity::class.java)
            newStoryLauncher.launch(addStoryIntent, optionsCompat)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.story_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_logout) {
            mainViewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvStoryList.layoutManager = layoutManager
        binding.rvStoryList.setHasFixedSize(true)

        storyListAdapter = StoryListAdapter()
        storyListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.rvStoryList.smoothScrollToPosition(0)
            }
        })

        binding.rvStoryList.adapter = storyListAdapter
    }

    private fun setStoryList(list: List<Story>) {
        storyListAdapter.submitList(list)
        storyListAdapter.setOnItemClickCallback(
            object: StoryListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Story) {
                    val detailIntent = Intent(this@MainActivity, StoryDetailActivity::class.java)
                    detailIntent.putExtra("story_id", data.id)
                    startActivity(detailIntent)
                }
            }
        )
    }

    private fun loadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.cpiLoading.visibility = View.VISIBLE
            binding.rvStoryList.visibility = View.INVISIBLE
        } else {
            binding.cpiLoading.visibility = View.GONE
            binding.rvStoryList.visibility = View.VISIBLE
        }
    }
}