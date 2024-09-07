package com.example.agrobot.ui

data class WeatherData(
    val main: MainData,
    val weather: List<WeatherDescription>,
    // ... other fields as needed
)

data class MainData(
    val temp: Double,
    // ... other fields like humidity, pressure, etc.
)

data class WeatherDescription(
    val description: String,
    val icon: String
    // ... other fields as needed
)