package com.example.weather.ui.maps

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.weather.R
import com.example.weather.ui.WeatherViewModel
import com.example.weather.util.APP_ACTIVITY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(R.layout.fragment_maps) {

    private var latitude = 47.8
    private var longitude = 35.1
    private lateinit var viewModel: WeatherViewModel
    private val callback = OnMapReadyCallback { googleMap ->

        val city = LatLng(latitude, longitude)
        googleMap.apply {
            addMarker(MarkerOptions().position(city).title("${city.latitude} : ${city.longitude}"))
            moveCamera(CameraUpdateFactory.newLatLng(city))
            animateCamera(CameraUpdateFactory.newLatLngZoom(city, 7f))
        }

        googleMap.setOnMapClickListener { latLng ->
            googleMap.apply {
                clear()
                animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5f))
                latitude = latLng.latitude
                longitude = latLng.longitude
                addMarker(MarkerOptions().position(latLng).title("${latLng.latitude} : ${latLng.longitude}"))
                viewModel.fetchWeatherForecastByLatLng(latitude.toString(), longitude.toString())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = APP_ACTIVITY.viewModel

        viewModel.weatherForecastDay.observe(viewLifecycleOwner, { response ->
            response.data?.let { weatherResponse ->
                latitude = weatherResponse[0].lat
                longitude = weatherResponse[0].lon
            }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}