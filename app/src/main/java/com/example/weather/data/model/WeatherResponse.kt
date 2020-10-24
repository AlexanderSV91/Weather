package com.example.weather.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("city")
    val city: City?,
    @SerializedName("list")
    val list: List<ListWeather>?,
) {

    data class City(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("coord")
        val coord: Coord?
    ) {
        class Coord {
            @SerializedName("lon")
            val lon = 0.0
            @SerializedName("lat")
            val lat = 0.0
        }
    }

    data class ListWeather(
        @SerializedName("dt") //Time of data forecasted, unix, UTC
        val dt: Int?,
        @SerializedName("dt_txt") //Time of data forecasted, ISO, UTC
        val dtTxt: String?,
        @SerializedName("main")
        val main: Main?,
        @SerializedName("sys")
        val sys: Sys?,
        @SerializedName("weather")
        val weather: List<Weather>?,
        @SerializedName("wind")
        val wind: Wind?
    ) {

        data class Main(
            @SerializedName("humidity")
            val humidity: Int?,
            @SerializedName("temp")
            val temp: Double?,
            @SerializedName("temp_max")
            val tempMax: Double?,
            @SerializedName("temp_min")
            val tempMin: Double?
        )

        data class Sys(
            @SerializedName("pod") //Part of the day (n - night, d - day)
            val pod: String?
        )

        data class Weather(
            @SerializedName("icon")
            val icon: String?,
            @SerializedName("id")
            val id: Int?
        )

        data class Wind(
            @SerializedName("deg")
            val deg: Int?,
            @SerializedName("speed")
            val speed: Double?
        )
    }
}
