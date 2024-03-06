package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).commit()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker to the map
        val markerLocation = LatLng(37.7749, -122.4194) // San Francisco coordinates
        val markerTitle = "San Francisco"
        map.addMarker(MarkerOptions().position(markerLocation).title(markerTitle))

        // Optionally, you can move the camera to the marker location
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(markerLocation, 10f))
    }
}