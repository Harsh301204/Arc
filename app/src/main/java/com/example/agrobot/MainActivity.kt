package com.example.agrobot

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.agrobot.ui.WeatherApiService
import com.example.agrobot.ui.WeatherData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var database2: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_services)

        database= FirebaseDatabase.getInstance().getReference("moisture")
        database1= FirebaseDatabase.getInstance().getReference("temperature")
        database2= FirebaseDatabase.getInstance().getReference("humidity")


        database.get()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.value.toString()
                    val progress=findViewById<ProgressBar>(R.id.progressBar)
                    val soilMoisture=findViewById<TextView>(R.id.textView_soilMoistureData)
                    soilMoisture.text= value
                    progress.progress= value?.toInt()!!
                    val comment=findViewById<TextView>(R.id.textView_comment)
                    val green=progress.progress*255/100.0
                    val red=255-green
                    val commentText=findViewById<TextView>(R.id.textView_commentText)
                    when (progress.progress) {
                        in 16..60 -> {
                            comment.text="${progress.progress}\nAVERAGE"
                            commentText.text="The soil moisture level is moderate, but regular monitoring is recommended to ensure optimal irrigation.\nKeep an eye on the readings to avoid over or under-watering."
                            comment.setTextColor(Color.rgb(red.toInt(),green.toInt(),0))
                        }
                        in 0..15-> {
                            comment.text = "${progress.progress}\nBAD"
                            comment.setTextColor(Color.rgb(red.toInt(),green.toInt(),0))
                            commentText.text =
                                "Warning: The soil moisture level is very low.\nImmediate action is required to prevent crop stress.\nAdjust your irrigation settings to restore balance."
                        }
                        else -> {
                            comment.text="${progress.progress}\nGOOD"
                            commentText.text="The soil moisture level is ideal for plant growth. No immediate irrigation is required.\nMaintain the current watering schedule to sustain healthy soil conditions."
                            comment.setTextColor(Color.rgb(red.toInt(),green.toInt(),0))
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("a", "Failed to read value.", error.toException())
                }
            })
            database1.get()
            database1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.value.toString()
                    val data = findViewById<TextView>(R.id.textView_temperatureData)
                    data.text = value

                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("a", "Failed to read value.", error.toException())
                }
            })

    database2.get()
            database2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.value.toString()
                    val data = findViewById<TextView>(R.id.textView_humidityData)
                    data.text = value
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("a", "Failed to read value.", error.toException())
                }
            })




        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherApiService = retrofit.create(WeatherApiService::class.java)
        val call = weatherApiService.getWeather("Ranchi", "7f91085215b8d45cf43025c210113209")
        call.enqueue(object : Callback<WeatherData> {

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    // Update UI with weatherData
                    val tempData = weatherData?.main?.temp
                    val weatherDescription = weatherData?.weather?.get(0)?.description
                    val weatherIcon = weatherData?.weather?.get(0)?.icon.toString()
                    val textWeatherData = findViewById<TextView>(R.id.textView_weatherData)
                    textWeatherData.text = weatherDescription.toString()
                    val weatherIconImageView : ImageView = findViewById(R.id.imageViewWeatherIcon)
                    val iconUrl = "http://openweathermap.org/img/wn/$weatherIcon@2x.png"
                    Log.d("Weather", "Icon URL: $iconUrl")
                    Picasso.get()
                        .load(iconUrl)
                        .error(R.drawable.weather_icon) // Add an error placeholder image if the load fails
                        .into(weatherIconImageView, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Log.d("Picasso", "Image loaded successfully")
                            }

                            override fun onError(e: Exception?) {
                                Log.e("Picasso", "Failed to load image: ${e?.message}")
                            }
                        })


                    Log.d("Weather", "Response: $weatherData")
                } else {
                    Log.e("Weather", "Error: ${response.code()} - ${response.message()}")
                    // Handle error
                }

            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.e("Weather", "Error: ${t.message}")
            }
        })
    }
}
