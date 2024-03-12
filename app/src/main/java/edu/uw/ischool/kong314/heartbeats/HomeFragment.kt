package edu.uw.ischool.kong314.heartbeats

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class HomeFragment() : Fragment(R.layout.fragment_home)  {
    private lateinit var newRV: RecyclerView
    private lateinit var newArrayList: ArrayList<Post>
    private lateinit var imageId: List<String>
    private lateinit var title: List<String>
    private lateinit var username: List<String>
    private lateinit var desc: List<String>
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private val TAG: String = "FromHome"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setBottomNavigationBarVisibility(View.VISIBLE)
        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        databaseRepo.getPostImages { imgs, imgError ->
            if (imgError != null) {
                Log.e(TAG, "Error retrieving post images: ${imgError.message}")
            } else {
                if (imgs != null) {
                    imageId = imgs.reversed()
                    databaseRepo.getPostUsers() { users, userError ->
                        if (userError != null) {
                            Log.e(TAG, "Error retrieving posts users: ${userError.message}")
                        } else {
                            if (users != null) {
                                username = users.reversed()
                                databaseRepo.getPostDesc() { descs, descError ->
                                    if (descError != null) {
                                        Log.e(TAG, "Error retrieving post descriptions: ${descError.message}")
                                    } else {
                                        if (descs != null) {
                                            desc = descs.reversed()
                                            databaseRepo.getPostTitles() { titles, titlesError ->
                                                if (titlesError != null) {
                                                    Log.e(TAG, "Error retrieving post titles: ${titlesError.message}")
                                                } else {
                                                    if (titles != null) {
                                                        title = titles.reversed()
                                                        getData()
                                                    } else {
                                                        Log.e(TAG, "No posts found.")
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "No posts found.")
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "No posts found.")
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "No posts found.")
                }
            }
        }


        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).addToBackStack(null).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment()).addToBackStack(null).commit()
        }



        newRV = view.findViewById<RecyclerView>(R.id.rvPosts)
        newRV.layoutManager = LinearLayoutManager(requireContext())
        newRV.setHasFixedSize(true)

        newArrayList = arrayListOf<Post>()
    }

    private fun getData() {
        val currUserUID = Firebase.auth.currentUser?.uid
        var friendsList = emptyList<String>()
        if(currUserUID != null) {
            databaseRepo.getFriends { friends, error ->
                if (error != null) {
                    Log.e(TAG, "Error retrieving post friend details: ${error.message}")
                } else {
                    if (friends != null) {
                        friendsList = friends

                    } else {
                        Log.e(TAG, "No friends found.")
                    }
                    for (i in title.indices) {
                        if (friendsList.contains(username[i])) {
                            val post = Post(title[i], desc[i], imageId[i], "@" + username[i])
                            newArrayList.add(post)
                        } else {
                            databaseRepo.getPrivacybyUsername(username[i]) { privacyState, privacyError ->
                                if (privacyError != null) {
                                    Log.e(
                                        TAG,
                                        "Error retrieving post privacy: ${privacyError.message}"
                                    )
                                } else {
                                    if (privacyState != null) {
                                        if (!privacyState) {
                                            val post = Post(
                                                title[i],
                                                desc[i],
                                                imageId[i],
                                                "@" + username[i]
                                            )
                                            newArrayList.add(post)
                                        } else {
                                            databaseRepo.getUsernameByUID(currUserUID) { currUsername, usernameError ->
                                                if (usernameError != null) {
                                                    Log.e(
                                                        TAG,
                                                        "Error retrieving current username: ${usernameError.message}"
                                                    )
                                                } else {
                                                    if (currUsername != null && username[i] == currUsername) {
                                                        val post = Post(
                                                            title[i],
                                                            desc[i],
                                                            imageId[i],
                                                            "@" + username[i]
                                                        )
                                                        newArrayList.add(post)
                                                    } else {
                                                        Log.e(TAG, "No username found.")
                                                    }
                                                    newRV.adapter = PostAdapter(newArrayList)
                                                }

                                            }
                                        }
                                    } else {
                                        Log.e(TAG, "No posts found.")
                                        newRV.adapter = PostAdapter(newArrayList)
                                    }
                                }
                            }

                        }

                    }

                }
            }
        }

    }
}