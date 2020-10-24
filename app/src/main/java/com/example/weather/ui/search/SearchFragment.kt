package com.example.weather.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.ui.WeatherViewModel
import com.example.weather.util.API_KEY_PLACE
import com.example.weather.util.APP_ACTIVITY
import com.example.weather.util.AUTOCOMPLETE_REQUEST_CODE
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class SearchFragment : Fragment(R.layout.search_fragment) {

    private lateinit var viewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = APP_ACTIVITY.viewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(APP_ACTIVITY.applicationContext, API_KEY_PLACE)
        }
        startActivitySearch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        viewModel.fetchWeatherForecastByCityName(place.name.toString()) {
                            findNavController().popBackStack()
                        }
                    }
                }
                Activity.RESULT_CANCELED -> {
                    findNavController().popBackStack()
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun startActivitySearch() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(this@SearchFragment.requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }
}