package edu.uw.ischool.kong314.heartbeats

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import java.util.PropertyPermission


class DailyFragment() : Fragment(R.layout.fragment_daily)  {
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private val TAG: String = "DailyFragment"
    private lateinit var challengesList: List<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gets the current location of the phone

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val fDate = dateFormat.format(currentTime)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val title = view.findViewById<EditText>(R.id.dailyTitle)
        val description = view.findViewById<EditText>(R.id.dailyDesc)
        val postBtn = view.findViewById<Button>(R.id.dailyPostBtn)

        val dateText = view.findViewById<TextView>(R.id.dailyDate)
        dateText.text = fDate

        val promptText = view.findViewById<TextView>(R.id.textView4)

        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        databaseRepo.getChallenges { challenges, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving challenges: ${error.message}")
            } else {
                if (challenges != null) {
                    challengesList = challenges
                } else {
                    Log.e(TAG, "No challenges found.")
                }
                val challengeIdx = (LocalDate.now().toEpochDay() % challengesList.size).toInt()
                promptText.text = challengesList.get(challengeIdx)
            }
        }

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        val textChange: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isEmpty()) {
                    postBtn.setEnabled(false)
                } else {
                    postBtn.setEnabled(true)
                }
            }
        }

        title.addTextChangedListener(textChange)
        description.addTextChangedListener(textChange)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment())
                .addToBackStack(null).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment())
                .addToBackStack(null).commit()
        }

        postBtn.setOnClickListener {
            getCurrentLocation(title.getText().toString(), description.getText().toString())
        }
    }

    private fun getCurrentLocation(title: String, description: String) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                lastLocation = it
                val database = FirebaseDatabase.getInstance().getReference("locations")
                val hashMap = HashMap<String, Any>()
                hashMap.put("title", title)
                hashMap.put("desc", description)
                hashMap.put("latitude", lastLocation.latitude)
                hashMap.put("longitude", lastLocation.longitude)
                Firebase.auth.currentUser?.let { it1 -> database.child(it1.uid).setValue(hashMap) }
            }
        }
    }
}