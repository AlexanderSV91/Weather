package com.example.weather.ui.main_screen

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.data.model.WeatherDayModel
import com.example.weather.databinding.CardItemWeatherDateBinding
import com.example.weather.util.dayConverter
import com.example.weather.util.setCurrentIconWeather
import com.example.weather.util.temperature
import kotlinx.android.synthetic.main.card_item_weather_date.view.*

class WeatherDayAdapter(private val listener: CellClickListener) :
    RecyclerView.Adapter<WeatherDayAdapter.WeatherViewHolder>() {

    private var weatherModel = mutableListOf<WeatherDayModel>()
    var view: View? = null

    inner class WeatherViewHolder(private val binding: CardItemWeatherDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = weatherModel[position]
                    listener.onCellClickListener(item, itemView, view)
                }
            }
        }

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(weather: WeatherDayModel) {
            binding.apply {
                tvDateItemWeatherDate.text = dayConverter(weather.dt.toLong(), true)
                tvTemperatureItemWeatherDate.text =
                    "${temperature(weather.tempMax)}/${temperature(weather.tempMin)}"
                ivIconItemWeatherDate.setImageResource(setCurrentIconWeather(weather.weatherTimeModel[0].weatherIcon))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = CardItemWeatherDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val weather = weatherModel[position]
        holder.bind(weather)
        if (position == 0) {
            view = holder.itemView
            view?.let { changeColorViewToBlue(it) }
        }
    }

    private fun changeColorViewToBlue(view: View) {
        val tvDate = view.tv_date_item_weather_date as TextView
        val tvTemperature = view.tv_temperature_item_weather_date as TextView
        val tvIcon = view.iv_icon_item_weather_date as ImageView
        tvDate.setTextColor(view.resources.getColor(R.color.colorPrimary, null))
        tvTemperature.setTextColor(view.resources.getColor(R.color.colorPrimary, null))
        tvIcon.setColorFilter(view.resources.getColor(R.color.colorPrimary, null))
    }

    override fun getItemCount(): Int = weatherModel.size

    interface CellClickListener {
        fun onCellClickListener(
            weatherDayModel: WeatherDayModel,
            view: View,
            lastViewAdapter: View?
        )
    }

    fun addList(weatherList: List<WeatherDayModel>) {
        weatherModel.clear()
        weatherModel.addAll(weatherList)
        notifyDataSetChanged()
    }
}
