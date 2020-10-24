package com.example.weather.data.model

data class WeatherDayModel(
    val cityId: Int,
    val cityName: String,
    val dt: Int,
    val dtTxt: String,
    val humidity: Int,
    var tempMax: Double,
    var tempMin: Double,
    val lon: Double,
    val lat: Double,
    val windDeg: Int,
    val windSpeed: Double,
    val weatherTimeModel: List<WeatherTimeModel>
)