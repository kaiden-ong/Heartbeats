package edu.uw.ischool.kong314.heartbeats

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment()).commit()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        databaseRepo.getLocations() { locations, error ->
            if (error != null) {
                println("Error retrieving locations: ${error.message}")
            } else {
                if (locations != null) {
                    println("Retrieved locations: $locations")
                    setMarkers(locations)
                } else {
                    println("No locations found.")
                }
            }
        }
    }

    private fun setMarkers(locations: Map<String, LatLng>) {
        for ((name, latLng) in locations) {
            val markerOptions = MarkerOptions().position(latLng).title(name)
            map.addMarker(markerOptions)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))
            }
        }
    }
}