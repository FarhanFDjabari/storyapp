package com.example.storyapp.ui.features.stories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.helper.wrapEspressoIdlingResource
import com.example.storyapp.ui.features.login.LoginActivity
import com.example.storyapp.ui.features.maps.MapsActivity
import com.example.storyapp.ui.features.new_story.NewStoryActivity
import com.example.storyapp.ui.features.stories.adapter.LoadingStateAdapter
import com.example.storyapp.ui.features.stories.adapter.StoryListAdapter
import com.example.storyapp.ui.features.stories.viewModel.MainViewModel
import com.example.storyapp.ui.features.story_detail.StoryDetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyListAdapter: StoryListAdapter
    private var isNewStoryUploaded: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Stories"

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

        mainViewModel.logout.observe(this) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

        mainViewModel.stories.observe(this) {
            storyListAdapter.submitData(lifecycle, it)
            setListAction()
        }

        val newStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                isNewStoryUploaded = it.data?.getBooleanExtra(
                    getString(R.string.is_new_story_uploaded),
                    false
                )
                if (isNewStoryUploaded == true) {
                    storyListAdapter.refresh()
                    binding.rvStoryList.smoothScrollToPosition(0)
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
        when(item.itemId) {
            R.id.item_logout -> {
                mainViewModel.logout()
            }
            R.id.item_map_view -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvStoryList.setHasFixedSize(true)

        storyListAdapter = StoryListAdapter().apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    if (positionStart == 0) {
                        binding.rvStoryList.smoothScrollToPosition(0)
                    }
                }
            })
        }

        binding.rvStoryList.layoutManager = layoutManager
        binding.rvStoryList.adapter = storyListAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyListAdapter.retry()
            }
        )
        storyListAdapter.refresh()

        wrapEspressoIdlingResource {
            lifecycleScope.launch {
                storyListAdapter.loadStateFlow.collect {
                    binding.cpiLoading.isVisible = it.refresh is LoadState.Loading
                    if (it.refresh is LoadState.Error) {
                        Snackbar.make(window.decorView.rootView, "Error when load stories", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setListAction() {
        storyListAdapter.setOnItemClickCallback(
            object: StoryListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Story) {
                    val detailIntent = Intent(this@MainActivity, StoryDetailActivity::class.java)
                    detailIntent.putExtra(getString(R.string.intent_key_story_id), data.id)
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