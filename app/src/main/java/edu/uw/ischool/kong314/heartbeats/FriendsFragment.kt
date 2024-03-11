package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

data class FriendData (
    val imgURL: String,
    val friendUser: String
)
class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private lateinit var database: DatabaseReference
    private lateinit var friendsNameList: List<String>
    private lateinit var usersList: List<String>
    private val TAG: String = "FriendFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNavigationView.menu.size()) {
            bottomNavigationView.menu.getItem(i).isChecked = false
        }
        bottomNavigationView.menu.setGroupCheckable(0, true, true)

        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment()).commit()
        }

        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        database = Firebase.database.reference
        databaseRepo.getFriends { friends, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving friends: ${error.message}")
            } else {
                if (friends != null) {
                    friendsNameList = friends
                    Log.d(TAG, friendsNameList.toString())
                } else {
                    Log.e(TAG, "No friends found.")
                }
                val friendsList = mutableListOf<FriendData>()
                for (friend in friendsNameList) {
                    friendsList.add(FriendData("https://example.com/img1.jpg", friend))
                }
                val container = view.findViewById<TableLayout>(R.id.friendsContainer)

                friendsList.forEach { entry ->
                    val row = TableRow(requireContext())

                    val padding = resources.getDimensionPixelSize(R.dimen.row_padding)
                    row.setPadding(padding, padding, 0, padding)

                    val friendImg = ImageView(requireContext())
                    friendImg.setImageResource(R.drawable.profile)

                    // Set layout parameters to increase the image size
                    val imageSize = 100
                    val params = TableRow.LayoutParams(imageSize, imageSize)
                    friendImg.layoutParams = params

                    row.addView(friendImg)

                    val space1 = TextView(requireContext())
                    space1.text = "        "
                    row.addView(space1)

                    val username = TextView(requireContext())
                    username.text = entry.friendUser
                    username.textSize = 20f
                    row.addView(username)

                    container.addView(row)
                }
            }
        }

        val addBtn = view.findViewById<Button>(R.id.addFriendBtn)
        addBtn.setOnClickListener {
            Log.d(TAG, "button pressed")
            val friendToAdd = view.findViewById<EditText>(R.id.friendSearch).text.toString()
            database.child("user_info").child(Firebase.auth.currentUser!!.uid)
                .child("username").get().addOnSuccessListener {
                if (friendToAdd == it.value) {
                    Toast.makeText(requireContext(), "Cannot friend yourself!", Toast.LENGTH_SHORT).show()
                } else if (friendsNameList.contains(friendToAdd)) {
                    Toast.makeText(requireContext(), "Already added this user!", Toast.LENGTH_SHORT).show()
                } else {
                    databaseRepo.getUsernames { users, error ->
                        if (error != null) {
                            Log.e(TAG, "Error retrieving friend: ${error.message}")
                        } else {
                            if (users != null) {
                                usersList = users
                                if (usersList.contains(friendToAdd)) {
                                    val newFriendsList = mutableListOf<String>()
                                    newFriendsList.add("dummy")
                                    newFriendsList.addAll(friendsNameList)
                                    newFriendsList.add(friendToAdd)
                                    database.child("user_info").child(Firebase.auth.currentUser!!.uid)
                                        .child("friends").setValue(newFriendsList)
                                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.container, FriendsFragment()).commit()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Username does not exist",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Log.e(TAG, "No users found.")
                            }
                        }
                    }
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
}