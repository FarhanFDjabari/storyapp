package com.example.storyapp.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.ui.features.login.viewModel.LoginViewModel
import com.example.storyapp.ui.features.new_story.viewModel.NewStoryViewModel
import com.example.storyapp.ui.features.register.viewModel.RegisterViewModel
import com.example.storyapp.ui.features.stories.viewModel.MainViewModel
import com.example.storyapp.ui.features.story_detail.viewModel.StoryDetailViewModel
import com.example.storyapp.ui.splash.SplashViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository): ViewModelProvider.NewInstanceFactory() {

        companion object {
            @Volatile
            private var instance: ViewModelFactory? = null

            @JvmStatic
            fun getInstance(context: Context): ViewModelFactory {
                if (instance == null) {
                    synchronized(ViewModelFactory::class.java) {
                        instance = ViewModelFactory(
                            Injection.provideUserRepository(context),
                            Injection.provideStoryRepository(context),
                        )
                    }
                }

                return instance as ViewModelFactory
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepository, userRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
            return NewStoryViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
            return StoryDetailViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}