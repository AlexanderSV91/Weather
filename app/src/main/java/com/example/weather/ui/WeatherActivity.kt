package com.example.weather.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weather.R
import com.example.weather.util.APP_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_weather.*

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity(R.layout.activity_weather) {

    val viewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        APP_ACTIVITY = this

        bottomNavigationView.setupWithNavController(weatherNavHostFragment.findNavController())
    }
}