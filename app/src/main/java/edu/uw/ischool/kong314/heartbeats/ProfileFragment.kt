package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment() : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profilePic = view.findViewById<ImageView>(R.id.profilePic)
        // change image here
        val profileUserText = view.findViewById<TextView>(R.id.profileName)
        // change username here
        val emailUserText = view.findViewById<TextView>(R.id.profileEmail)

        val pointText = view.findViewById<TextView>(R.id.pointText)

        val signoutBtn = view.findViewById<Button>(R.id.signoutBtn)
        signoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut();
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SigninFragment()).commit()
        }

    }
}