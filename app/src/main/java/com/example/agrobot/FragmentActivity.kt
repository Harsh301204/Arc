package com.example.agrobot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.agrobot.ui.WeatherApiService
import com.example.agrobot.ui.WeatherData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigationView)
        replaceFragment(Home())
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.video -> replaceFragment(LiveStreaming())
                R.id.our_team -> replaceFragment(OurTeam())
                R.id.gemini -> replaceFragment(Gemini())

                else -> {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }


    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}