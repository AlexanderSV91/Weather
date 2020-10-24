package com.example.weather.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather.WeatherApplication
import com.example.weather.data.WeatherRepository
import com.example.weather.data.model.WeatherDayModel
import com.example.weather.data.model.WeatherResponse
import com.example.weather.data.model.WeatherTimeModel
import com.example.weather.util.APP_ACTIVITY
import com.example.weather.util.CITY
import com.example.weather.util.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class WeatherViewModel @ViewModelInject constructor(
    app: Application,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(app) {

    private lateinit var locationManager: LocationManager
    private lateinit var location: LocationListener

    val weatherForecastDay: MutableLiveData<Resource<List<WeatherDayModel>>> = MutableLiveData()
    private var weatherDayModel = mutableListOf<WeatherDayModel>()

    init {
        if (weatherDayModel.size == 0) {
            fetchWeatherForecastByCityName(CITY) {
            }
        }
    }

    fun fetchWeatherForecastByLatLng(lat: String, lng: String) = viewModelScope.launch {
        safeWeatherForecastCall { weatherRepository.fetchWeatherForecastByLatLng(lat, lng) }
    }

    fun fetchWeatherForecastByCityName(cityName: String, function: () -> Unit) =
        viewModelScope.launch {
            safeWeatherForecastCall { weatherRepository.fetchWeatherForecastByCityName(cityName) }
            function()
        }

    private suspend fun safeWeatherForecastCall(function: suspend () -> Response<WeatherResponse>) {
        weatherForecastDay.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = function()
                weatherForecastDay.postValue(handleWeatherForecastResponse(response))
            } else {
                weatherForecastDay.postValue(Resource.Error("Нет соединения с интернетом"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherForecastDay.postValue(Resource.Error("Ошибка сети"))
                else -> weatherForecastDay.postValue(Resource.Error("Ошибка преобразования"))
            }
        }
    }

    private fun handleWeatherForecastResponse(response: Response<WeatherResponse>): Resource<List<WeatherDayModel>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                checkSizeModel()
                initDayModel(resultResponse)
                return Resource.Success(weatherDayModel)
            }
        }
        return Resource.Error(response.message())
    }

    private fun initTimeModel(response: WeatherResponse, dayTimeStr: String):
            List<WeatherTimeModel> {
        val weatherTimeModel = mutableListOf<WeatherTimeModel>()
        if (response.list != null) {
            for (listWeather in response.list) {
                val date = listWeather.dtTxt?.split(" ")?.get(0)
                if (date == dayTimeStr) {
                    val dtTxt = listWeather.dtTxt
                    val temp = listWeather.main?.temp ?: -1.0
                    if (listWeather.weather != null) {
                        for (weather in listWeather.weather) {
                            val weatherIcon = weather.icon ?: ""
                            weatherTimeModel.add(
                                WeatherTimeModel(
                                    dtTxt,
                                    temp,
                                    weatherIcon,
                                )
                            )
                        }
                    }
                }
            }
        }
        return weatherTimeModel
    }

    private fun initDayModel(response: WeatherResponse) {
        var tMax = Double.MIN_VALUE
        var tMin = Double.MAX_VALUE
        var iteratorDayShort = ""
        val cityName = response.city?.name ?: ""
        val cityId = response.city?.id ?: -1
        val coord = response.city?.coord
        val list = response.list
        if (list != null) {
            for (listW in list) {
                val dt = listW.dt ?: -1
                val dtTxt = listW.dtTxt ?: ""
                val humidity = listW.main?.humidity ?: -1
                val tempMax = listW.main?.tempMax ?: -1.0
                val tempMin = listW.main?.tempMin ?: -1.0
                val deg = listW.wind?.deg ?: -1
                val speed = listW.wind?.speed ?: -1.0
                val day = dtTxt.split(" ")[0]
                if (iteratorDayShort.isEmpty() || iteratorDayShort != day) {
                    val weatherTimeModel = initTimeModel(response, dtTxt.split(" ")[0])
                    weatherDayModel.add(
                        WeatherDayModel(
                            cityId,
                            cityName,
                            dt,
                            dtTxt,
                            humidity,
                            tempMax,
                            tempMin,
                            coord?.lon ?: 35.1,
                            coord?.lat ?: 47.8,
                            deg,
                            speed,
                            weatherTimeModel
                        )
                    )
                    iteratorDayShort = day
                } else {
                    if (tMax < tempMax) {
                        weatherDayModel[weatherDayModel.lastIndex].tempMax = tempMax
                        tMax = tempMax
                    }
                    if (tMin > tempMin) {
                        weatherDayModel[weatherDayModel.lastIndex].tempMin = tempMin
                        tMin = tempMin
                    }
                }
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WeatherApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getLocation(function: () -> Unit) {
        locationManager = APP_ACTIVITY.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {
            if (ActivityCompat.checkSelfPermission(
                    APP_ACTIVITY, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    APP_ACTIVITY, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            location = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    fetchWeatherForecastByLatLng(p0.latitude.toString(), p0.longitude.toString())
                    function()
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                }

                override fun onProviderEnabled(provider: String?) {
                }

                override fun onProviderDisabled(provider: String?) {
                }
            }

            if (hasGps) {
                locationManager
                    .requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 500.2F, location
                    )
            }
            if (hasNetwork) {
                locationManager
                    .requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000, 500.2F, location
                    )
            }
        } else {
            startActivity(APP_ACTIVITY, Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), null)
        }
    }

    private fun checkSizeModel() {
        if (weatherDayModel.size != 0) {
            weatherDayModel.clear()
        }
    }

    fun removeListener() {
        locationManager.removeUpdates(location)
    }
}
