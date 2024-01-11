package com.example.weatherapp.model

data class DataDay(
    val country: String,
    val city: String,
    val time: String,
    val condition: String,
    val imageWeather: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val hours: String
)
