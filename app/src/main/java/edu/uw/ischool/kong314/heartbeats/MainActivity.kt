package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.ArrayDeque
import java.util.Deque


class MainActivity : AppCompatActivity() {
    val homeFragment = HomeFragment()
    val mapFragment = MapFragment()
    val postFragment = PostFragment()
    val dailyFragment = DailyFragment()
    val leaderboardFragment = LeaderboardFragment()
    private var deque : Deque<Int> = ArrayDeque(5)
    private var flag =  true

    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        deque.push(R.id.home)
        loadFragment(homeFragment)
        val navbar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navbar.run {
            selectedItemId = R.id.home
            setOnItemSelectedListener { item ->
                val id = item.itemId
                if (deque.contains(id)) {
                    if (id == R.id.home) {
                        if (deque.size != 1) {
                            if (flag) {
                                deque.addFirst(R.id.home)
                                flag = false
                            }
                        }
                    }
                    deque.remove(id)
                }
                deque.push(id)
                loadFragment(getFragment(item.itemId))
                true
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, SigninFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
        }

//        navbar.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.home -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, homeFragment)
//                        .addToBackStack(null).commit()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.map -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, mapFragment)
//                        .addToBackStack(null).commit()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.post -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, postFragment)
//                        .addToBackStack(null).commit()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.daily -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, dailyFragment)
//                        .addToBackStack(null).commit()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.leaderboard -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container, leaderboardFragment)
//                        .addToBackStack(null).commit()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            false
//        }
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            deque.pop()
            if (!deque.isEmpty()) {
                deque.peek()?.let {
                    getFragment(it)
                }?.let {
                    loadFragment(it)
                }
            } else {
                finishAffinity()
            }
        }
    }

    private fun getFragment(itemId: Int): Fragment {
        val bn = findViewById<BottomNavigationView>(R.id.bottom_navigation).menu
        when (itemId) {
            R.id.home -> {
                bn.getItem(0).isChecked = true
                return homeFragment
            }

            R.id.map -> {
                bn.getItem(1).isChecked = true
                return mapFragment
            }

            R.id.post -> {
                bn.getItem(2).isChecked = true
                return postFragment
            }

            R.id.daily -> {
                bn.getItem(3).isChecked = true
                return dailyFragment
            }

            R.id.leaderboard -> {
                bn.getItem(4).isChecked = true
                return leaderboardFragment
            }
        }
        bn.getItem(0).isChecked = true
        return homeFragment
    }

    private fun loadFragment(frag: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, frag, frag.javaClass.simpleName)
            .commit()
    }

    fun setBottomNavigationBarVisibility(visibility: Int) {
        findViewById<View>(R.id.bottom_navigation).visibility = visibility
    }
}