package com.example.agrobot.auth

data class Users(val Email: String? = null, val Password: String? = null){
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}
