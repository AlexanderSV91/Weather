package com.example.weather.util

import com.example.weather.BuildConfig
import com.example.weather.ui.WeatherActivity

lateinit var APP_ACTIVITY: WeatherActivity
const val AUTOCOMPLETE_REQUEST_CODE = 1
const val LOCATION_PERMISSION_CODE = 2
const val API_KEY = BuildConfig.OPENWEATHERMAP_ACCESS_KEY
const val API_KEY_PLACE = BuildConfig.GOOGLE_PLACES_ACCESS_KEY
const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val METRIC = "metric"
const val CITY = "Запорожье"
const val LANG = "ru"