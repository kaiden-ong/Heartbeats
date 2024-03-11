package edu.uw.ischool.kong314.heartbeats

import android.icu.text.CaseMap.Title
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView


class HomeFragment() : Fragment(R.layout.fragment_home)  {
    private lateinit var newRV: RecyclerView
    private lateinit var newArrayList: ArrayList<Post>
    lateinit var imageId: Array<Int>
    lateinit var title: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).setBottomNavigationBarVisibility(View.VISIBLE)

        // dummy data for now
        imageId = arrayOf(
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo
        )

        title = arrayOf(
            "The cat chased the mouse around the garden.",
            "She sang beautifully at the concert last night.",
            "The old house on the hill looked haunted.",
            "I can't believe it's already March!",
            "He laughed nervously during the interview."
        )

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

        newRV = view.findViewById<RecyclerView>(R.id.rvPosts)
        newRV.layoutManager = LinearLayoutManager(requireContext())
        newRV.setHasFixedSize(true)

        newArrayList = arrayListOf<Post>()
        getData()
    }

    private fun getData() {
        for (i in imageId.indices) {
            val post = Post(title[i], imageId[i])
            newArrayList.add(post)
        }
        newRV.adapter = PostAdapter(newArrayList)
    }
}