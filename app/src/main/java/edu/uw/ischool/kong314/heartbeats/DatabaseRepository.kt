package edu.uw.ischool.kong314.heartbeats

import android.location.Location
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.google.android.gms.maps.model.LatLng

interface DatabaseRepository {
    fun getChallenges(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getLocations(callback: (Map<String, LatLng>?, DatabaseError?) -> Unit)
}

class DatabaseRepositoryStorage() : DatabaseRepository {
    private val db = Firebase.database

    override fun getChallenges(callback: (List<String>?, DatabaseError?) -> Unit) {
        val challengeRef = db.getReference("daily_challenge")
        val challenges = mutableListOf<String>()
        challengeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val challenge = item.getValue(String::class.java)
                    challenge?.let { challenges.add(it) }
                }
                callback(challenges, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getLocations(callback: (Map<String, LatLng>?, DatabaseError?) -> Unit) {
        val locationRef = db.getReference("locations")
        val locations = mutableMapOf<String, LatLng>()
        locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val latitude = item.child("latitude").getValue(Double::class.java)
                    val longitude = item.child("longitude").getValue(Double::class.java)
                    val name = item.child("name").getValue(String::class.java)
                    if (latitude != null && longitude != null && name != null) {
                        val latLng = LatLng(latitude, longitude)
                        locations[name] = latLng
                    }
                }
                callback(locations, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }
}