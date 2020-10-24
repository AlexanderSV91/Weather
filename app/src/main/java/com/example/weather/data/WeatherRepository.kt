package com.example.weather.data

import com.example.weather.api.WeatherApi
import com.example.weather.util.API_KEY
import com.example.weather.util.LANG
import com.example.weather.util.METRIC
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(private val weatherApi: WeatherApi) {

    suspend fun fetchWeatherForecastByCityName(cityName: String) =
        weatherApi.getFivesDayWeatherForecastByCityName(cityName, METRIC, LANG, API_KEY)

    suspend fun fetchWeatherForecastByLatLng(lat: String, lng: String) =
        weatherApi.getFivesDayWeatherForecastByLatLng(lat, lng, METRIC, LANG, API_KEY)

}