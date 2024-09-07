package com.example.agrobot.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.R
import com.example.agrobot.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var name: String
    private lateinit var rePassword: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        database = Firebase.database.reference
        setContentView(R.layout.activity_sign_up)
        val signUp=findViewById<Button>(R.id.sign_up_btn)
        signUp.setOnClickListener {
            email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            name = findViewById<EditText>(R.id.editTextName).text.toString()
            rePassword=findViewById<EditText>(R.id.editTextReenterPassword).text.toString()
            if(name.isBlank()||email.isBlank()||password.isBlank()||rePassword.isBlank()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if(password!=rePassword){
                Toast.makeText(this, "password does not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                createAccount(email,password)
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account created successfully",Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Account creation failed",Toast.LENGTH_SHORT).show()
                Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
                Log.d("Account","createAccount: Failure",task.exception)
            }
        }
    }
    //save data into database
    private fun saveUserData() {
        email = findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
        password = findViewById<EditText>(R.id.editTextPassword).text.toString()
        name = findViewById<EditText>(R.id.editTextName).text.toString()
        rePassword=findViewById<EditText>(R.id.editTextReenterPassword).text.toString()
        val user= UserModel(email,password,name)
        database.child("users").child(auth.currentUser!!.uid).setValue(user)
        val userId:String=FirebaseAuth.getInstance().currentUser!!.uid
        database.child("users").child(userId).setValue(user)
    }
}