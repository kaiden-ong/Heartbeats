package edu.uw.ischool.kong314.heartbeats

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ProfileFragment() : Fragment(R.layout.fragment_profile) {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendBtn = view.findViewById<ImageView>(R.id.imageView)

        friendBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, FriendsFragment()).commit()
        }

        (requireActivity() as MainActivity).setBottomNavigationBarVisibility(View.VISIBLE)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.menu.setGroupCheckable(0, true, false)
        for (i in 0 until bottomNavigationView.menu.size()) {
            bottomNavigationView.menu.getItem(i).isChecked = false
        }
        bottomNavigationView.menu.setGroupCheckable(0, true, true)

        auth = Firebase.auth
        database = Firebase.database.reference

        val profilePic = view.findViewById<ImageView>(R.id.profilePic)
        // change image here
        val profileUserText = view.findViewById<TextView>(R.id.profileName)
        database.child("user_info").child(auth.currentUser!!.uid).child("username")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                val username = dataSnapshot.getValue(String::class.java)
                profileUserText.text = "@" + username.toString()
            }
            .addOnFailureListener { exception ->
                Log.e("RetrieveUsername", "Error getting username", exception)
            }
        val emailUserText = view.findViewById<TextView>(R.id.profileEmail)
        emailUserText.text = "Email: " + auth.currentUser!!.email

        val pointText = view.findViewById<TextView>(R.id.pointText)
        database.child("user_info").child(auth.currentUser!!.uid).child("heartbeats")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                val heartbeats = dataSnapshot.getValue(Int::class.java)
                pointText.text = "Heartbeats: " + heartbeats.toString()
            }
            .addOnFailureListener { exception ->
                Log.e("RetrieveHeartbeats", "Error getting heartbeats", exception)
            }

        val privacyBtn = view.findViewById<Button>(R.id.privacyBtn)
        privacyBtn.setOnClickListener {
            val showPopUp = PopupFragment()
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
        }

        val signoutBtn = view.findViewById<Button>(R.id.signoutBtn)
        signoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SigninFragment()).commit()
        }

    }
}