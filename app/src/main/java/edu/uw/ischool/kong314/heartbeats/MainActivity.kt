package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Vector


class MainActivity : AppCompatActivity() {
    val homeFragment = HomeFragment()
    val mapFragment = MapFragment()
    val postFragment = PostFragment()
    val dailyFragment = DailyFragment()
    val leaderboardFragment = LeaderboardFragment()
    val friendsFragment = FriendsFragment()

    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, SigninFragment()).commit()
        } else {
            val navbar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()

            navbar.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, homeFragment).commit()
                        return@setOnItemSelectedListener true
                    }

                    R.id.map -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, mapFragment).commit()
                        return@setOnItemSelectedListener true
                    }

                    R.id.post -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, postFragment).commit()
                        return@setOnItemSelectedListener true
                    }

                    R.id.daily -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, dailyFragment).commit()
                        return@setOnItemSelectedListener true
                    }

                    R.id.leaderboard -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, leaderboardFragment).commit()
                        return@setOnItemSelectedListener true
                    }
                }
                false
            }
        }
    }

    fun setBottomNavigationBarVisibility(visibility: Int) {
        findViewById<View>(R.id.bottom_navigation).visibility = visibility
    }
}