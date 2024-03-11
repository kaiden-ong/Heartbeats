package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class DailyFragment() : Fragment(R.layout.fragment_daily)  {
    private lateinit var heartbeatsApp: HeartbeatsApp
    private lateinit var databaseRepo: DatabaseRepository
    private val TAG: String = "DailyFragment"
    private lateinit var challengesList: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val fDate = dateFormat.format(currentTime)

        val dateText = view.findViewById<TextView>(R.id.dailyDate)
        dateText.text = fDate

        val promptText = view.findViewById<TextView>(R.id.textView4)

        heartbeatsApp = requireActivity().application as HeartbeatsApp
        databaseRepo = heartbeatsApp.databaseRepository
        databaseRepo.getChallenges { challenges, error ->
            if (error != null) {
                Log.e(TAG, "Error retrieving challenges: ${error.message}")
            } else {
                if (challenges != null) {
                    challengesList = challenges
                } else {
                    Log.e(TAG, "No challenges found.")
                }
                val challengeIdx = (LocalDate.now().toEpochDay() % challengesList.size).toInt()
                promptText.text = challengesList.get(challengeIdx)
            }
        }

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)
        val profileBtn = view.findViewById<ImageView>(R.id.imageView2)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment())
                .addToBackStack(null).commit()
        }

        profileBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, ProfileFragment())
                .addToBackStack(null).commit()
        }
    }


}