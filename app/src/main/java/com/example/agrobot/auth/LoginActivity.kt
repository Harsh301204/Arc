package com.example.agrobot.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.FragmentActivity
import com.example.agrobot.MainActivity
import com.example.agrobot.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        val signUpButton = findViewById<TextView>(R.id.sign_up_btn)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        val forgotPasswordButton = findViewById<TextView>(R.id.forgotPasswordTextView)
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        val loginButton = findViewById<TextView>(R.id.log_in_btn)
        loginButton.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent)
//            val email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
//            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
//            if(email.isNotEmpty() && password.isNotEmpty()){
//                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }else {
//                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
//            }
        }


    }
}