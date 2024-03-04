package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class HomeFragment() : Fragment(R.layout.fragment_home)  {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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