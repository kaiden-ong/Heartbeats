package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView

data class rankingEntry(
    val rank: Int,
    val username: String,
    val points: Int
)

class LeaderboardFragment() : Fragment(R.layout.fragment_leaderboard)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tempLeaderboardData = listOf(
            rankingEntry(1, "User1", 100),
            rankingEntry(2, "User2", 90),
            rankingEntry(3, "User3", 80),
            rankingEntry(4, "User4", 70),
            rankingEntry(5, "User5", 60),
            rankingEntry(6, "User6", 50),
            rankingEntry(7, "User7", 40),
            rankingEntry(8, "User8", 30),
            rankingEntry(9, "User9", 20),
            rankingEntry(10, "User10", 10)
        )

        val container = view.findViewById<LinearLayout>(R.id.leaderboard_container)



        val titleRow = TableRow(requireContext())
        val rankTitleView = TextView(requireContext())
        rankTitleView.text = "RANK"
        titleRow.addView(rankTitleView)

        val space1 = TextView(requireContext())
        space1.text = "        "
        titleRow.addView(space1)

        val userTitleView = TextView(requireContext())
        userTitleView.text = "USERNAME"
        titleRow.addView(userTitleView)
        val space2 = TextView(requireContext())
        space2.text = "                        "
        titleRow.addView(space2)

        val pointsTitleView = TextView(requireContext())
        pointsTitleView.text = "POINTS"
        titleRow.addView(pointsTitleView)
        container.addView(titleRow)

        tempLeaderboardData.forEach { entry ->
            val row = TableRow(requireContext())
            val rankText = TextView(requireContext())
            rankText.text = entry.rank.toString()
            row.addView(rankText)

            val space1 = TextView(requireContext())
            space1.text = "        "
            row.addView(space1)

            val userText = TextView(requireContext())
            userText.text = entry.username
            row.addView(userText)

            val space2 = TextView(requireContext())
            space2.text = "                        "
            row.addView(space2)

            val pointsText = TextView(requireContext())
            pointsText.text = entry.points.toString()
            row.addView(pointsText)

            container.addView(row)
        }

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment()).commit()
        }
    }
}