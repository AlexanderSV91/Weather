package com.example.weather.util

import android.annotation.SuppressLint
import com.example.weather.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun dayConverter(time: Long, isShort: Boolean): String {
    val converter = if (isShort) {
        SimpleDateFormat("EEE", Locale.getDefault())
    } else {
        SimpleDateFormat("EEE, d MMMM", Locale.getDefault())
    }
    converter.timeZone = TimeZone.getTimeZone("UTC")

    return converter.format(Date(time * 1000))
}

fun temperature(temp: Double) = "${temp.toInt()}\u00B0"

fun windDeg(deg: Int) = when (deg) {
    in 1..89 -> R.drawable.icon_wind_ne
    90 -> R.drawable.icon_wind_e
    in 91..179 -> R.drawable.icon_wind_se
    180 -> R.drawable.icon_wind_s
    in 181..269 -> R.drawable.icon_wind_ws
    270 -> R.drawable.icon_wind_w
    in 271..359 -> R.drawable.icon_wind_wn
    360, 0 -> R.drawable.icon_wind_n
    else -> R.drawable.icon_wind_s
}

fun setCurrentIconWeather(url: String?) = when (url) {
    "01d", "01n" -> R.drawable.ic_white_day_bright
    "02d", "02n" -> R.drawable.ic_white_day_cloudy
    "03d", "03n" -> R.drawable.ic_white_day_cloudy
    "04d", "04n" -> R.drawable.ic_white_day_cloudy
    "09d", "09n" -> R.drawable.ic_white_day_shower
    "10d", "10n" -> R.drawable.ic_white_day_rain
    "11d", "11n" -> R.drawable.ic_white_day_thunder
    else -> R.drawable.ic_white_day_bright
}

fun setIconItemWeatherTime(url: String?) = when (url) {
    "01d" -> R.drawable.ic_white_day_bright
    "01n" -> R.drawable.ic_white_night_bright
    "02d" -> R.drawable.ic_white_day_cloudy
    "02n" -> R.drawable.ic_white_night_cloudy
    "03d" -> R.drawable.ic_white_day_cloudy
    "03n" -> R.drawable.ic_white_night_cloudy
    "04d" -> R.drawable.ic_white_day_cloudy
    "04n" -> R.drawable.ic_white_night_cloudy
    "09d" -> R.drawable.ic_white_day_shower
    "09n" -> R.drawable.ic_white_night_shower
    "10d" -> R.drawable.ic_white_day_rain
    "10n" -> R.drawable.ic_white_night_rain
    "11d" -> R.drawable.ic_white_day_thunder
    "11n" -> R.drawable.ic_white_night_thunder
    else -> R.drawable.ic_white_day_bright
}
