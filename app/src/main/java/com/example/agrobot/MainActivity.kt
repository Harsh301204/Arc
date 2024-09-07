package com.example.agrobot

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.agrobot.ui.WeatherApiService
import com.example.agrobot.ui.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        // Initialize Firebase database references
        database = FirebaseDatabase.getInstance().getReference("moisture")
        database1 = FirebaseDatabase.getInstance().getReference("temperature")
        database2 = FirebaseDatabase.getInstance().getReference("humidity")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // If permission is already granted, get the current location
            getCurrentLocation()
        }

        // Firebase database listeners
        setupFirebaseListeners()
        // Call weather API
        fetchWeatherData("Ranchi")
    }
    private var place=""
    private fun getCityAndState(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val city = addresses[0].locality ?: "Unknown city"
                val state = addresses[0].adminArea ?: "Unknown state"
                val cityState = "$city, $state"

                // Update the city TextView here with city and state
                val cityTextView = findViewById<TextView>(R.id.textView_weather)
                cityTextView.text = cityState
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Failed to get city and state: ${e.message}")
        }
    }



    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    getCityAndState(it.latitude, it.longitude)
                }
            }.addOnFailureListener {
                Log.e("Location", "Failed to get location: ${it.message}")
            }
        } else {
            Log.e("Permission", "Location permission not granted")
        }
    }

    private fun setupFirebaseListeners() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val soilMoisture = findViewById<TextView>(R.id.textView_soilMoistureData)
        val comment = findViewById<TextView>(R.id.textView_comment)
        val commentText = findViewById<TextView>(R.id.textView_commentText)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value.toString()
                soilMoisture.text = value
                progressBar.progress = value.toInt()
                updateCommentSection(progressBar.progress, comment, commentText)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading moisture data: ${error.message}")
            }
        })

        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temperature = dataSnapshot.value.toString()
                findViewById<TextView>(R.id.textView_temperatureData).text = temperature
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading temperature data: ${error.message}")
            }
        })

        database2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val humidity = dataSnapshot.value.toString()
                findViewById<TextView>(R.id.textView_humidityData).text = humidity
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading humidity data: ${error.message}")
            }
        })
    }

    private fun updateCommentSection(progress: Int, comment: TextView, commentText: TextView) {
        val green = progress * 255 / 100.0
        val red = 255 - green
        when (progress) {
            in 16..60 -> {
                comment.text = "$progress\nAVERAGE"
                commentText.text =
                    "The soil moisture level is moderate, but regular monitoring is recommended."
                comment.setTextColor(Color.rgb(red.toInt(), green.toInt(), 0))
            }
            in 0..15 -> {
                comment.text = "$progress\nBAD"
                commentText.text = "Warning: The soil moisture level is very low."
                comment.setTextColor(Color.rgb(red.toInt(), green.toInt(), 0))
            }
            else -> {
                comment.text = "$progress\nGOOD"
                commentText.text = "The soil moisture level is ideal for plant growth."
                comment.setTextColor(Color.rgb(red.toInt(), green.toInt(), 0))
            }
        }
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApiService = retrofit.create(WeatherApiService::class.java)
        val call = weatherApiService.getWeather(city, "7f91085215b8d45cf43025c210113209")
        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val weatherDescription = weatherData?.weather?.get(0)?.description
                    val weatherIcon = weatherData?.weather?.get(0)?.icon
                    updateWeatherUI(weatherDescription, weatherIcon)
                } else {
                    Log.e("Weather", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.e("Weather", "Error: ${t.message}")
            }
        })
    }

    private fun updateWeatherUI(weatherDescription: String?, weatherIcon: String?) {
        findViewById<TextView>(R.id.textView_weatherData).text = weatherDescription ?: "N/A"
    }
}
