package edu.uw.ischool.kong314.heartbeats

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log

interface DatabaseRepository {
    fun getChallenges(callback: (List<String>?, DatabaseError?) -> Unit)
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
}