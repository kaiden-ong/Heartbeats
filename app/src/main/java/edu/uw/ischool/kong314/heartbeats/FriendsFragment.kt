package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

data class FriendData (
    val imgURL: String,
    val friendUser: String
)
class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private lateinit var friendsNameList: List<String>
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
    }
}