package com.example.storyapp.ui.features.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.ui.features.maps.viewModel.MapsViewModel
import com.example.storyapp.ui.features.story_detail.StoryDetailActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import okio.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var binding: ActivityMapsBinding
    private var selectedStoryId: String? = null
    private var selectedMark: Marker? = null
    private var prevSelectedMark: Marker? = null
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isPickLocation: Boolean = false
    private val initialMarker: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Stories Map"

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isPickLocation = intent.getBooleanExtra(
            getString(R.string.extra_is_pick_location),
            false
        )

        if (isPickLocation) {
            supportActionBar?.subtitle = "Pick location from map"
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val factory = ViewModelFactory.getInstance(application)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMapClickListener {
            selectedStoryId = null
            binding.btnViewDetail.isVisible = false
            binding.btnPickLocation.isVisible = false
        }

        mMap.setOnMarkerClickListener {
            it.showInfoWindow()
            selectedStoryId = it.tag.toString()
            prevSelectedMark = selectedMark
            selectedMark = it
            if (isPickLocation) {
                binding.btnPickLocation.isVisible = true
            }
            binding.btnViewDetail.isVisible = true
            if (selectedStoryId.isNullOrEmpty() || selectedStoryId == "null") {
                binding.btnViewDetail.visibility = View.INVISIBLE
            }
            return@setOnMarkerClickListener true
        }

        if (isPickLocation) {
            mMap.setOnMapLongClickListener {
                addNewMarker(it)
                binding.btnPickLocation.isVisible = true
                binding.btnViewDetail.visibility = View.INVISIBLE
            }
        }

        setMapStyle()

        mapsViewModel.isLoading.observe(this) {
            loadingState(it)
        }

        mapsViewModel.snackbarText.observe(this) {
            Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        }

        mapsViewModel.stories.observe(this) { stories ->
            if (stories.isNotEmpty()) {
                addMarkers(stories)
            }
        }

        mapsViewModel.getAllStories()

        binding.btnViewDetail.setOnClickListener {
            val detailIntent = Intent(this, StoryDetailActivity::class.java)
            detailIntent.putExtra(getString(R.string.intent_key_story_id), selectedStoryId)
            startActivity(detailIntent)
        }

        binding.btnPickLocation.setOnClickListener {
            backPressedCallback.handleOnBackPressed()
        }

        getUserLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]?: false -> {
                    getUserLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]?: false -> {
                    getUserLocation()
                }
                else -> {
                    // when permissions denied
                }
            }
        }

    private fun addMarkers(stories: List<Story>) {
        stories.forEach {
            if (it.lat != null && it.lon != null) {
                val coor = LatLng(it.lat, it.lon)
                initialMarker.add(coor)
                mMap.addMarker(MarkerOptions().position(coor).title(it.name).snippet(it.description))
                    ?.tag = it.id
                boundsBuilder.include(coor)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
            bounds,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
            300
        ))
    }

    private fun addNewMarker(latLng: LatLng) {
        if (initialMarker.contains(selectedMark?.position)) {
            prevSelectedMark?.remove()
        } else {
            selectedMark?.remove()
        }
        selectedMark = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .snippet(getAddress(latLng.latitude, latLng.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        selectedMark?.showInfoWindow()
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, "Failed parse map style", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(this, "Can't find map style: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddress(lat: Double?, lon: Double?): String {
        var addressName: String? = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            if (lat != null && lon != null) {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    addressName = list[0].getAddressLine(0)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressName?: "($lat, $lon)"
    }

    private fun getUserLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val latLng = LatLng(it.latitude, it.longitude)
                    if (isPickLocation) {

                        addNewMarker(latLng)
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                        )
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Last location is not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.pbLoading.visibility = View.VISIBLE
        } else {
            binding.pbLoading.visibility = View.GONE
        }
    }

    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intent.putExtras(
                Bundle().apply {
                    putParcelable(
                        getString(R.string.intent_key_location),
                        selectedMark?.position
                    )
                    putString(
                        getString(R.string.intent_key_address),
                        getAddress(
                            selectedMark?.position?.latitude,
                            selectedMark?.position?.longitude
                        )
                    )
                }
            )
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                backPressedCallback.handleOnBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}