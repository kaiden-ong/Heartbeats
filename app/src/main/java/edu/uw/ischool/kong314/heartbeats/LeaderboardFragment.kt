package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat

data class rankingEntry(
    val rank: Int,
    val username: String,
    val points: Int
)

class LeaderboardFragment() : Fragment(R.layout.fragment_leaderboard)  {
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private lateinit var userList: Map<String, Int>
    private val TAG: String = "LeaderboardFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rankingList = mutableListOf<rankingEntry>()

        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        databaseRepo.getUserHeartbeats() { users, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving challenges: ${error.message}")
            } else {
                if (users != null) {
                    userList = users
                } else {
                    Log.e(TAG, "No challenges found.")
                }
                val sortedUsers = userList.entries.sortedByDescending {it.value}.map { it.key}
                sortedUsers.forEachIndexed { index, user ->
                    rankingList.add(rankingEntry(index + 1, user, userList.get(user)!!))
                }
                setUITable(rankingList)
            }
        }

        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredUsers = if(query.isNotEmpty()) {
                    userList.filter { it.key.contains(query, ignoreCase = true)}
                } else {
                    userList
                }

                val newRankingList = mutableListOf<rankingEntry>()
                val newRankings = filteredUsers.entries.sortedByDescending { it.value }.map {it.key}
                newRankings.forEachIndexed { index, user ->
                    newRankingList.add(rankingEntry(index + 1, user, filteredUsers.get(user)!!))
                }
                setUITable(newRankingList)
            }
        })



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

    override fun onStop() {
        super.onStop()
        val searchBar = view?.findViewById<EditText>(R.id.search_bar)
        searchBar?.setText("")
    }

    private fun setUITable(rankingList: List<rankingEntry>) {
        val container = view?.findViewById<LinearLayout>(R.id.leaderboard_container)

        container?.removeAllViews()

        val titleRow = TableRow(requireContext())
        val rankTitleView = TextView(requireContext())
        rankTitleView.text = "RANK"
        rankTitleView.textSize = 18f
        titleRow.addView(rankTitleView)

        val space1 = TextView(requireContext())
        space1.text = "        "
        titleRow.addView(space1)

        val userTitleView = TextView(requireContext())
        userTitleView.text = "USERNAME"
        userTitleView.textSize = 18f
        titleRow.addView(userTitleView)
        val space2 = TextView(requireContext())
        space2.text = "      "
        titleRow.addView(space2)

        val pointsTitleView = TextView(requireContext())
        pointsTitleView.text = "HEARTBEATS"
        pointsTitleView.textSize = 18f
        titleRow.addView(pointsTitleView)
        container?.addView(titleRow)

        val divider = View(requireContext())
        val params = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 4)
        divider.layoutParams = params
        val color = ContextCompat.getColor(requireContext(), R.color.black)
        divider.setBackgroundColor(color)
        container?.addView(divider)

        rankingList.forEach { entry ->
            val row = TableRow(requireContext())
            val rankText = TextView(requireContext())
            rankText.text = entry.rank.toString()
            rankText.textSize = 18f
            row.addView(rankText)

            val space1 = TextView(requireContext())
            space1.text = "        "
            row.addView(space1)

            val userText = TextView(requireContext())
            userText.text = entry.username
            userText.textSize = 18f
            row.addView(userText)

            val space2 = TextView(requireContext())
            space2.text = "      "
            row.addView(space2)

            val pointsText = TextView(requireContext())
            pointsText.text = entry.points.toString()
            pointsText.textSize = 18f
            row.addView(pointsText)

            container?.addView(row)




            val rowSpaceView = View(requireContext())
            val rowSpaceParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 20
            )
            rowSpaceView.layoutParams = rowSpaceParams
            container?.addView(rowSpaceView)
        }
    }
}