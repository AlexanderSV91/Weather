package com.example.weather.ui.main_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weather.R
import com.example.weather.data.model.WeatherDayModel
import com.example.weather.databinding.WeatherFragmentBinding
import com.example.weather.ui.WeatherViewModel
import com.example.weather.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.card_item_weather_date.view.*

@AndroidEntryPoint
@SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
class WeatherFragment : Fragment(R.layout.weather_fragment), WeatherDayAdapter.CellClickListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherDayAdapter: WeatherDayAdapter
    private lateinit var weatherTimeAdapter: WeatherTimeAdapter
    private lateinit var imageViewMyLocation: ImageView
    private var _binding: WeatherFragmentBinding? = null
    private val binding get() = _binding!!
    private var lastView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = WeatherFragmentBinding.bind(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = APP_ACTIVITY.viewModel

        setupRecycleView()

        viewModel.weatherForecastDay.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { weatherResponse ->
                        weatherDayAdapter.addList(weatherResponse)
                        weatherTimeAdapter.addList(weatherResponse[0].weatherTimeModel)
                        mainScreen(weatherResponse[0])
                        lastView?.let { changeColorViewToBlack(it) }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Произошла ошибка: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        binding.ivLocationWeatherFragment.setOnClickListener {
            imageViewMyLocation = it as ImageView
            enableImageView(R.drawable.ic_my_location_red, false)
            getPermissions()
        }
    }

    private fun getPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_PERMISSION_CODE
            )
        } else {
            viewModel.getLocation {
                enableImageView(R.drawable.ic_my_location, true)
                viewModel.removeListener()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.getLocation {
                        enableImageView(R.drawable.ic_my_location, true)
                        viewModel.removeListener()
                    }
                }
                return
            }
        }
    }

    private fun enableImageView(drawable: Int, isEnable: Boolean) {
        imageViewMyLocation.setImageDrawable(resources.getDrawable(drawable, null))
        imageViewMyLocation.isEnabled = isEnable
    }

    private fun mainScreen(weather: WeatherDayModel) {
        binding.apply {
            tvLocationWeatherFragment.text = weather.cityName
            tvDayWeatherFragment.text = dayConverter(weather.dt.toLong(), false)
            tvShowCurrentTemperatureWeatherFragment.text =
                "${temperature(weather.tempMax)}/${temperature(weather.tempMin)}"
            tvShowCurrentHumidityWeatherFragment.text = "${weather.humidity}%"
            tvShowCurrentWindWeatherFragment.text = "${weather.windSpeed.toInt()}м/сек"
            ivShowCurrentWindDirectionWeatherFragment.setImageResource(windDeg(weather.windDeg))
            ivShowCurrentIconWeatherFragment.setImageResource(setCurrentIconWeather(weather.weatherTimeModel[0].weatherIcon))

            if (ivPlaceWeatherFragment.visibility == View.INVISIBLE) {
                ivPlaceWeatherFragment.visibility = View.VISIBLE
                ivLocationWeatherFragment.visibility = View.VISIBLE
                ivShowCurrentTemperatureWeatherFragment.visibility = View.VISIBLE
                ivShowCurrentHumidityWeatherFragment.visibility = View.VISIBLE
                ivShowCurrentWindWeatherFragment.visibility = View.VISIBLE
            }
        }
    }

    override fun onCellClickListener(
        weatherDayModel: WeatherDayModel,
        view: View,
        lastViewAdapter: View?
    ) {
        mainScreen(weatherDayModel)
        weatherTimeAdapter.addList(weatherDayModel.weatherTimeModel)
        if (lastViewAdapter != null) {
            changeColorViewToBlack(lastViewAdapter)
            weatherDayAdapter.view = null
        }
        chooseWeather(view)
    }

    private fun chooseWeather(view: View) {
        lastView?.let { changeColorViewToBlack(it) }

        changeColorViewToBlue(view)
        lastView = view
    }

    private fun changeColorViewToBlack(view: View) {
        val tvDateLast = view.tv_date_item_weather_date as TextView
        val tvTemperatureLast = view.tv_temperature_item_weather_date as TextView
        val tvIconLast = view.iv_icon_item_weather_date as ImageView
        tvDateLast.setTextColor(resources.getColor(R.color.black, null))
        tvTemperatureLast.setTextColor(resources.getColor(R.color.black, null))
        tvIconLast.setColorFilter(resources.getColor(R.color.black, null))
    }

    private fun changeColorViewToBlue(view: View) {
        val tvDate = view.tv_date_item_weather_date as TextView
        val tvTemperature = view.tv_temperature_item_weather_date as TextView
        val tvIcon = view.iv_icon_item_weather_date as ImageView
        tvDate.setTextColor(resources.getColor(R.color.colorPrimary, null))
        tvTemperature.setTextColor(resources.getColor(R.color.colorPrimary, null))
        tvIcon.setColorFilter(resources.getColor(R.color.colorPrimary, null))
    }

    private fun setupRecycleView() {
        binding.rvShowWeatherDate.apply {
            setHasFixedSize(true)
            weatherDayAdapter = WeatherDayAdapter(this@WeatherFragment)
            adapter = weatherDayAdapter
        }
        binding.rvShowWeatherTime.apply {
            setHasFixedSize(true)
            weatherTimeAdapter = WeatherTimeAdapter()
            adapter = weatherTimeAdapter
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}