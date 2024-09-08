package com.example.agrobot.auth

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.FragmentActivity
import com.example.agrobot.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database.reference
        val signUpButton = findViewById<TextView>(R.id.sign_up_btn)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordTextView)
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
            finish()
        }
        val loginButton = findViewById<TextView>(R.id.log_in_btn)
        loginButton.setOnClickListener {
            email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            if(email.isBlank()||password.isBlank()){
                Toast.makeText(this,"Please fill all details",Toast.LENGTH_SHORT).show()
            }
            else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
                    if(task.isSuccessful){
                        val user=auth.currentUser
                        updateUi(user)
                    }
                    else{
                        Toast.makeText(this,"Authentication failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, FragmentActivity::class.java))
    }
}