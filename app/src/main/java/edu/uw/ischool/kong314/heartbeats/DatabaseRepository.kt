package edu.uw.ischool.kong314.heartbeats

import android.location.Location
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.auth

interface DatabaseRepository {
    fun getChallenges(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getLocations(callback: (Map<String, LatLng>?, DatabaseError?) -> Unit)
    fun getUserHeartbeats(callback: (Map<String, Int>?, DatabaseError?) -> Unit)
    fun getUserPrivacy(callback: (Map<String, Boolean>?, DatabaseError?) -> Unit)

    fun setUserPrivacy(user: String, privacyState: Boolean)
    fun getFriends(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getUsernames(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getPostImages(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getPostUsers(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getPostDesc(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getPostTitles(callback: (List<String>?, DatabaseError?) -> Unit)
    fun getUsernamesByUID(callback: (Map<String, String>?, DatabaseError?) -> Unit)
    fun getPrivacybyUsername(username: String, callback: (Boolean?, DatabaseError?) -> Unit)
    fun getUsernameByUID(UID: String, callback: (String?, DatabaseError?) -> Unit)
}

class DatabaseRepositoryStorage() : DatabaseRepository {
    private val db = Firebase.database
    private val TAG: String = "databaseRepoStorage"

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

    override fun getUserHeartbeats(callback: (Map<String, Int>?, DatabaseError?) -> Unit) {
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
                for (item in snapshot.children) {
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

    override fun getUsernames(callback: (List<String>?, DatabaseError?) -> Unit) {
        val userRef = db.getReference("user_info")
        val users = mutableListOf<String>()
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val username = item.child("username").getValue(String::class.java)
                    if (username != null) {
                        users.add(username)
                    }
                }
                callback(users, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getPostImages(callback: (List<String>?, DatabaseError?) -> Unit) {
        val postImgRef = db.getReference("Posts")
        val imgs = mutableListOf<String>()
        postImgRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    imgs.add(item.child("image").getValue(String::class.java)!!)
                }
                callback(imgs, null)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getPostUsers(callback: (List<String>?, DatabaseError?) -> Unit) {
        val postUsersRef = db.getReference("Posts")
        val usernames = mutableListOf<String>()
        var usersByUID: Map<String, String> = emptyMap()
        this.getUsernamesByUID { users, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving users for posts: ${error.message}")
            } else {
                if (users != null) {
                    usersByUID = users
                } else {
                    Log.e(TAG, "No users found.")
                }
                postUsersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(item in snapshot.children) {
                            val uid = item.child("by").getValue(String::class.java)!!
                            usernames.add(usersByUID[uid]!!)
                        }
                        callback(usernames, null)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error)
                    }
                })
            }
        }

    }

    override fun getPostDesc(callback: (List<String>?, DatabaseError?) -> Unit) {
        val postDescRef = db.getReference("Posts")
        val descs = mutableListOf<String>()
        postDescRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    descs.add(item.child("desc").getValue(String::class.java)!!)
                }
                callback(descs, null)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getPostTitles(callback: (List<String>?, DatabaseError?) -> Unit) {
        val postTitleRef = db.getReference("Posts")
        val titles = mutableListOf<String>()
        postTitleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    titles.add(item.child("title").getValue(String::class.java)!!)
                }
                callback(titles, null)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getUsernamesByUID(callback: (Map<String, String>?, DatabaseError?) -> Unit) {
        val userInfoRef = db.getReference("user_info")
        val userUID = mutableMapOf<String, String>()

        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    val currUID = item.key
                    val username = item.child("username").getValue(String::class.java)
                    if (username != null) {
                        userUID[currUID!!] =  username
                    }
                }
                callback(userUID, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getPrivacybyUsername(username: String,
        callback: (Boolean?, DatabaseError?) -> Unit
    ) {
        val userInfoRef = db.getReference("user_info")
        var userUID = ""
        var usersPrivacy: Map<String, Boolean> = emptyMap()
        this.getUserPrivacy { users, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving users privacy: ${error.message}")
            } else {
                if (users != null) {
                    usersPrivacy = users
                } else {
                    Log.e(TAG, "No users found.")
                }
                userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(item in snapshot.children) {
                            val currUsername = item.child("username").getValue(String::class.java)
                            if (currUsername != null && currUsername == username) {
                                userUID = item.key.toString()
                            }
                        }
                        callback(usersPrivacy[userUID], null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error)
                    }
                })
            }
        }

    }

    override fun getUsernameByUID(UID: String, callback: (String?, DatabaseError?) -> Unit) {
        val userInfoRef = db.getReference("user_info")
        var username = ""

        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    if (item.key == UID) {
                        username = item.child("username").getValue(String::class.java).toString()
                    }
                }
                callback(username, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error)
            }
        })
    }

    override fun getFriends(callback: (List<String>?, DatabaseError?) -> Unit) {
        val friendsRef = db.getReference("user_info").child(Firebase.auth.currentUser!!.uid).child("friends")
        val friends = mutableListOf<String>()
        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children) {
                    friends.add(item.getValue(String::class.java)!!)
                }
                friends.remove("dummy")
                callback(friends, null)
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