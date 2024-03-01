package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

data class friendData(
    val imgURL: String,
    val friendUser: String
)
class FriendsFragment : Fragment(R.layout.fragment_friends) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // to be replaced by database later
        val friendsList = listOf(
            friendData("https://example.com/img1.jpg", "Friend 1"),
            friendData("https://example.com/img2.jpg", "Friend 2"),
            friendData("https://example.com/img3.jpg", "Friend 3"),
            friendData("https://example.com/img4.jpg", "Friend 4"),
            friendData("https://example.com/img5.jpg", "Friend 5"),
            friendData("https://example.com/img6.jpg", "Friend 6"),
            friendData("https://example.com/img7.jpg", "Friend 7"),
            friendData("https://example.com/img8.jpg", "Friend 8"),
            friendData("https://example.com/img9.jpg", "Friend 9"),
            friendData("https://example.com/img10.jpg", "Friend 10"),
            friendData("https://example.com/img10.jpg", "Friend 11"),
            friendData("https://example.com/img10.jpg", "Friend 12"),
            friendData("https://example.com/img10.jpg", "Friend 13"),
            friendData("https://example.com/img10.jpg", "Friend 14")
        )

        val container = view.findViewById<TableLayout>(R.id.friendsContainer)

        friendsList.forEach { entry ->
            val row = TableRow(requireContext())

            val padding = resources.getDimensionPixelSize(R.dimen.row_padding)
            row.setPadding(0, padding, 0, padding)
            val friendImg = ImageView(requireContext())
            friendImg.setImageResource(R.drawable.friends)
            row.addView(friendImg)
            val space1 = TextView(requireContext())
            space1.text = "        "
            row.addView(space1)
            val username = TextView(requireContext())
            username.text = entry.friendUser
            row.addView(username)
            container.addView(row)
        }
    }
}