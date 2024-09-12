package com.example.agrobot

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.agrobot.ui.WeatherApiService
import com.example.agrobot.ui.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import android.Manifest
import android.location.Location
import android.widget.Button
import com.google.firebase.database.values

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference
    private val button = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = button.getReference("water_motor_status")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context =requireContext()
        val myActivity = activity
        database = FirebaseDatabase.getInstance().getReference("moisture")
        database1 = FirebaseDatabase.getInstance().getReference("temperature")
        database2 = FirebaseDatabase.getInstance().getReference("humidity")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//        val button2=view.findViewById<Button>(R.id.button)
//        button2.setOnClickListener{
//            databaseReference.setValue(true)
//        }
//        val button3=view.findViewById<Button>(R.id.button2)
//        button3.setOnClickListener{
//            databaseReference.setValue(false)
//        }
        // Request location permissions if not granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (myActivity != null) {
                ActivityCompat.requestPermissions(
                    myActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
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
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        try {
            val addresses: List<Address>? = geocoder?.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val city = addresses[0].locality ?: "Unknown city"
                val state = addresses[0].adminArea ?: "Unknown state"
                val cityState = "$city, $state"

                // Update the city TextView here with city and state
                val cityTextView = view?.findViewById<TextView>(R.id.textView_weather)
                if (cityTextView != null) {
                    cityTextView.text = cityState
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Failed to get city and state: ${e.message}")
        }
    }



    private fun getCurrentLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
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
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBar)
        val soilMoisture = view?.findViewById<TextView>(R.id.textView_soilMoistureData)
        val comment = view?.findViewById<TextView>(R.id.textView_comment)
        val commentText = view?.findViewById<TextView>(R.id.textView_commentText)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value.toString()
                if (soilMoisture != null) {
                    soilMoisture.text = value
                }
                progressBar?.progress = value.toInt()
                if (progressBar != null) {
                    if (comment != null) {
                        if (commentText != null) {
                            updateCommentSection(progressBar.progress, comment, commentText)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading moisture data: ${error.message}")
            }
        })

        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val temperature = dataSnapshot.value.toString()
                view?.findViewById<TextView>(R.id.textView_temperatureData)?.text  = temperature
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading temperature data: ${error.message}")
            }
        })

        database2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val humidity = dataSnapshot.value.toString()
                view?.findViewById<TextView>(R.id.textView_humidityData)?.text = humidity
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
                    "The soil moisture level is moderate,but regular\nmonitoring is recommended to ensure optimal irrigation.\nKeep an eye on the readings to avoid over or under-watering."
                comment.setTextColor(Color.rgb(red.toInt(), green.toInt(), 0))
            }
            in 0..15 -> {
                comment.text = "$progress\nBAD"
                commentText.text = "Warning: The soil moisture level is too low.\nImmediate action is required to prevent crop stress.\nAdjust your irrigation settings to restore balance."
                comment.setTextColor(Color.rgb(red.toInt(), green.toInt(), 0))
            }
            else -> {
                comment.text = "$progress\nGOOD"
                commentText.text = "The soil moisture level is ideal for plant growth.\nNo immediate irrigation is required.\nMaintain the current watering schedule to sustain\nhealthy soil conditions."
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
        view?.findViewById<TextView>(R.id.textView_weatherData)?.text = weatherDescription ?: "N/A"
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}