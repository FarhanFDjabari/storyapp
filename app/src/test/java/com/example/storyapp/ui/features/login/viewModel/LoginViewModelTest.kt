package com.example.storyapp.ui.features.login.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storyapp.helper.MainDispatcherRule
import com.example.storyapp.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `when Login, should not throw error`() = runTest {
        val loginViewModel = LoginViewModel(userRepository)

        loginViewModel.login("email@email.com", "password")
        Mockito.verify(userRepository).login("email@email.com", "password")
    }

}