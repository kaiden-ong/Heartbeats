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
    fun getUserInfo(callback: (Map<String, Int>?, DatabaseError?) -> Unit)
    fun getUserPrivacy(callback: (Map<String, Boolean>?, DatabaseError?) -> Unit)

    fun setUserPrivacy(user: String, privacyState: Boolean)
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
                for (item in snapshot.children) {
                    val name = item.key
                    val latitude = item.child("latitude").getValue(Double::class.java)
                    val longitude = item.child("longitude").getValue(Double::class.java)

                    if (name != null && latitude != null && longitude != null) {
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

    override fun getUserInfo(callback: (Map<String, Int>?, DatabaseError?) -> Unit) {
        val userRef = db.getReference("user_info")
        val users = mutableMapOf<String, Int>()
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val username = item.child("username").getValue(String::class.java)
                    val heartbeats = item.child("heartbeats").getValue(Int::class.java)
                    if (username != null && heartbeats != null) {
                        users[username] = heartbeats
                    }
                }
                callback(users, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getUserPrivacy(callback: (Map<String, Boolean>?, DatabaseError?) -> Unit) {
        val userRef = db.getReference("user_info")
        val users = mutableMapOf<String, Boolean>()
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val uid = item.key
                    val privacy = item.child("privacy").getValue(Boolean::class.java)
                    if (uid != null && privacy != null) {
                        users[uid] = privacy
                    }
                }
                callback(users, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun setUserPrivacy(user: String, privacyState: Boolean) {
        val userRef = db.getReference("user_info/$user/privacy")
        userRef.setValue(privacyState)
    }
}