package com.example.weather.ui.main_screen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.data.model.WeatherTimeModel
import com.example.weather.databinding.CardItemWeatherTimeBinding
import com.example.weather.util.setIconItemWeatherTime
import com.example.weather.util.temperature

class WeatherTimeAdapter : RecyclerView.Adapter<WeatherTimeAdapter.WeatherViewHolder>() {

    private var weatherModel = mutableListOf<WeatherTimeModel>()

    class WeatherViewHolder(private val binding: CardItemWeatherTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(weather: WeatherTimeModel) {
            binding.apply {
                val time = weather.dtTxt.split(" ")[1].substring(0, 5)
                tvTimeItemWeatherTime.text = time
                tvTemperatureItemWeatherTime.text = temperature(weather.temp)
                val weatherIcon = if ("15:00" == time) {
                    weather.weatherIcon.replace("n", "d")
                } else {
                    weather.weatherIcon
                }
                ivIconItemWeatherTime.setImageResource(setIconItemWeatherTime(weatherIcon))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = CardItemWeatherTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherModel[position]
        holder.bind(weather)
    }

    override fun getItemCount() = weatherModel.size

    fun addList(weatherList: List<WeatherTimeModel>) {
        weatherModel.clear()
        weatherModel.addAll(weatherList)
        notifyDataSetChanged()
    }
}