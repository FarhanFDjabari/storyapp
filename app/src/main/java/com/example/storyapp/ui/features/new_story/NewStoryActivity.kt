package com.example.storyapp.ui.features.new_story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityNewStoryBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.helper.createCustomTempFile
import com.example.storyapp.helper.uriToFile
import com.example.storyapp.ui.features.new_story.viewModel.NewStoryViewModel
import com.google.android.material.R
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.Snackbar
import java.io.File

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var newStoryViewModel: NewStoryViewModel
    private var selectedPhoto: File? = null
    private var selectedPhotoPath: String? = null
    private var isNewStoryUploaded: Boolean = false

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Permission not granted",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        supportActionBar?.title = "Add New Story"

        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val factory = ViewModelFactory.getInstance(application)
        newStoryViewModel = ViewModelProvider(this, factory)[NewStoryViewModel::class.java]

        newStoryViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        newStoryViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_LONG).show()
        }

        newStoryViewModel.newStoryUploaded.observe(this) {
            binding.etDescription.text?.clear()
            binding.ivThumbnailPreview.setImageURI(null)
            binding.ivThumbnailPreview.setImageBitmap(null)
            isNewStoryUploaded = true
            backPressedCallback.handleOnBackPressed()
        }

        binding.btnUpload.setOnClickListener {
            uploadData()
        }

        binding.btnCameraOpen.setOnClickListener {
            startCamera()
        }

        binding.btnGalleryOpen.setOnClickListener {
            startGallery()
        }

        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)

    }

    private val launcherIntentGallery = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                selectedPhoto = uriToFile(selectedImg, this)
                binding.ivThumbnailPreview.setImageURI(selectedImg)
            }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            selectedPhoto = selectedPhotoPath?.let { it1 -> File(it1) }
            val result = BitmapFactory.decodeFile(selectedPhoto?.path)
            binding.ivThumbnailPreview.setImageBitmap(result)
        }
    }

    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intent.putExtra("is_new_story_uploaded", isNewStoryUploaded)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.storyapp",
                it
            )
            selectedPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uploadData() {
        with(binding) {
            if (!etDescription.text.isNullOrEmpty() && selectedPhoto != null) {
                newStoryViewModel.addNewStory(
                    description = etDescription.text?.trim().toString(),
                    photo = selectedPhoto!!,
                    lat = null,
                    lon = null,
                )
            } else {
                etDescription.error = "Description or image should not empty"
            }
        }
    }

    private fun loadingState(isLoading: Boolean) {
        val progressIndicatorSpec = CircularProgressIndicatorSpec(
            this,
            null,
            0,
            R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )

        progressIndicatorSpec.indicatorColors = intArrayOf(getColor(R.color.primary_text_disabled_material_light))

        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(this, progressIndicatorSpec).apply {
            setVisible(true, true)
        }

        if (isLoading) {
            binding.btnUpload.isEnabled = false
            binding.btnUpload.icon = progressIndicatorDrawable
        } else {
            binding.btnUpload.isEnabled = true
            binding.btnUpload.icon = null
        }
    }

    override fun onResume() {
        super.onResume()
        if (selectedPhoto != null) binding.ivThumbnailPreview.setImageURI(Uri.fromFile(selectedPhoto))
//        if (latLng != null) binding.tvLocation.text =
//            getString(R.string.latlon_format, latLng!!.latitude, latLng!!.longitude)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backPressedCallback.handleOnBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}