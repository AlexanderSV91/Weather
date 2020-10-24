package com.example.weather.api

import com.example.weather.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    suspend fun getFivesDayWeatherForecastByCityName(
        @Query("q") cityName: String,
        @Query("units") metric: String,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getFivesDayWeatherForecastByLatLng(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") metric: String,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>

}