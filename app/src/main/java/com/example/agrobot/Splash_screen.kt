package com.example.agrobot

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.auth.LoginActivity

@Suppress("DEPRECATION")
class Splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // HERE WE ARE TAKING THE REFERENCE OF OUR IMAGE
        // SO THAT WE CAN PERFORM ANIMATION USING THAT IMAGE
        val backgroundImage: ImageView = findViewById(R.id.iv1)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        backgroundImage.startAnimation(slideAnimation)
        Handler().postDelayed({
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        },3000)
    }
}