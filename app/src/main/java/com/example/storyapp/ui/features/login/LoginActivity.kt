package com.example.storyapp.ui.features.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.ui.features.login.viewModel.LoginViewModel
import com.example.storyapp.ui.features.register.RegisterActivity
import com.example.storyapp.ui.features.stories.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val factory = ViewModelFactory.getInstance(application)

        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        loginViewModel.isAuthenticated.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }

        binding.btnLogin.setOnClickListener {
            with (binding) {
                if (etEmail.text.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                if (etPassword.text.isNullOrEmpty()) {
                    return@setOnClickListener
                }

                loginViewModel.login(
                    email = etEmail.text?.trim().toString(),
                    password = etPassword.text?.trim().toString(),
                )
            }
        }

        val spanWord: Spannable =
            SpannableString(getString(R.string.register_here))
        spanWord.setSpan(
            ForegroundColorSpan(getColor(R.color.teal_700)),
            getString(R.string.register_here).length - "register here".length,
            getString(R.string.register_here).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvSpanSignup.text = spanWord

        binding.tvSpanSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        playAnimation()
    }

    private fun setupView() {
        with(binding) {
            ivHeaderIcon.alpha = 0f
            tvTitle.alpha = 0f
            tvSpanSignup.alpha = 0f
            etEmail.alpha = 0f
            etPassword.alpha = 0f
            btnLogin.alpha = 0f
        }
    }

    private fun playAnimation() {
        val headerImg = ObjectAnimator.ofFloat(binding.ivHeaderIcon, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val welcomeMsg = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val signupMsg = ObjectAnimator.ofFloat(binding.tvSpanSignup, View.ALPHA, 1f).setDuration(500)
        val emailEt = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEt = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)

        val togetherWelcomeSignup = AnimatorSet().apply {
            playTogether(welcomeMsg, signupMsg)
        }

        val togetherEmailPassword = AnimatorSet().apply {
            playTogether(emailEt, passwordEt)
        }

        AnimatorSet().apply {
            playSequentially(headerImg, togetherWelcomeSignup, togetherEmailPassword, loginBtn)
        }.start()
    }

    private fun loadingState(isLoading: Boolean) {
        val progressIndicatorSpec = CircularProgressIndicatorSpec(
            this,
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )

        progressIndicatorSpec.indicatorColors = intArrayOf(getColor(com.google.android.material.R.color.primary_text_disabled_material_light))

        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(this, progressIndicatorSpec).apply {
            setVisible(true, true)
        }

        if (isLoading) {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.icon = progressIndicatorDrawable
        } else {
            binding.btnLogin.isEnabled = true
            binding.btnLogin.icon = null
        }
    }
}