package com.example.storyapp.ui.features.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.ui.features.register.viewModel.RegisterViewModel
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val factory = ViewModelFactory.getInstance(application)

        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        registerViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        registerViewModel.isRegistered.observe(this) {
            if (it) {
                finish()
            }
        }

        registerViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }

        binding.btnRegister.setOnClickListener {
            with (binding) {
                if (etName.text.isNullOrEmpty()) {
                    tilName.error = "Name must not be empty"
                    return@setOnClickListener
                }
                if (etEmail.text.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                if (etPassword.text.isNullOrEmpty()) {
                    return@setOnClickListener
                }

                registerViewModel.register(
                    name = etName.text?.trim().toString(),
                    email = etEmail.text?.trim().toString(),
                    password = etPassword.text?.trim().toString(),
                )
            }
        }

        val spanWord: Spannable =
            SpannableString(getString(R.string.login_here))
        spanWord.setSpan(
            ForegroundColorSpan(getColor(R.color.teal_700)),
            getString(R.string.login_here).length - "login here".length,
            getString(R.string.login_here).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvSpanLogin.text = spanWord

        binding.tvSpanLogin.setOnClickListener {
            finish()
        }

        playAnimation()
    }

    private fun setupView() {
        with(binding) {
            ivHeaderIcon.alpha = 0f
            tvTitle.alpha = 0f
            tvSpanLogin.alpha = 0f
            etEmail.alpha = 0f
            tilName.alpha = 0f
            etPassword.alpha = 0f
            btnRegister.alpha = 0f
        }
    }

    private fun playAnimation() {
        val headerImg = ObjectAnimator.ofFloat(binding.ivHeaderIcon, View.ALPHA, 1f).setDuration(500)
        val loginBtn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val welcomeMsg = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val loginMsg = ObjectAnimator.ofFloat(binding.tvSpanLogin, View.ALPHA, 1f).setDuration(500)
        val nameTil = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(500)
        val emailEt = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEt = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)

        val togetherWelcomeLogin = AnimatorSet().apply {
            playTogether(welcomeMsg, loginMsg)
        }

        val togetherNameEmailPassword = AnimatorSet().apply {
            playTogether(nameTil, emailEt, passwordEt)
        }

        AnimatorSet().apply {
            playSequentially(headerImg, togetherWelcomeLogin, togetherNameEmailPassword, loginBtn)
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
            binding.btnRegister.isEnabled = false
            binding.btnRegister.icon = progressIndicatorDrawable
        } else {
            binding.btnRegister.isEnabled = true
            binding.btnRegister.icon = null
        }
    }
}