package edu.uw.ischool.kong314.heartbeats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    val homeFragment = HomeFragment()
    val mapFragment = MapFragment()
    val postFragment = PostFragment()
    val dailyFragment = DailyFragment()
    val leaderboardFragment = LeaderboardFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navbar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction().replace(R.id.container,homeFragment).commit()

        navbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container,homeFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.map -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container,mapFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.post -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container,postFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.daily -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container,dailyFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.leaderboard -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container,leaderboardFragment).commit()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}