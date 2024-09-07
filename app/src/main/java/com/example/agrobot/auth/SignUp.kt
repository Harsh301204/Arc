package com.example.agrobot.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        val signUp=findViewById<Button>(R.id.sign_up_btn)

        signUp.setOnClickListener {
            val email=findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val name=findViewById<EditText>(R .id.editTextName).text.toString()
            val password=findViewById<EditText>(R.id.editTextPassword).text.toString()
            val repeatPassword=findViewById<EditText>(R.id.editTextReenterPassword).text.toString()
            if (password == repeatPassword) {
                // Passwords match, proceed with registration
                database=FirebaseDatabase.getInstance().getReference("users")
                val user = Users(name, email, password)
                database.child(email).child("data").setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}