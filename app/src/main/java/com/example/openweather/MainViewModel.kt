package com.example.openweather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.*
import com.example.openweather.model.WeatherData
import com.example.openweather.network.ApiClient
import com.example.openweather.network.OpenWeatherMapService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.text.Typography.dagger

class MainViewModel : ViewModel() {

    private val openWeatherMapService: OpenWeatherMapService = ApiClient.instance!!.create(OpenWeatherMapService::class.java)

    fun getCurrentLocation(mainActivity: MainActivity)  {
        if (ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestLauncher = mainActivity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    getWeather(mainActivity)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && mainActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                ) {

                }
            }
            requestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            println("why")
        }
        else{
            getWeather(mainActivity)
        }
    }
    @SuppressLint("MissingPermission")
    fun getWeather(mainActivity: MainActivity) = viewModelScope.launch {
        val locationServices=LocationServices.getFusedLocationProviderClient(mainActivity)
        val result=locationServices.lastLocation.await()
        println("${result.latitude} : ${result.longitude}")
        openWeatherMapService.getCurrentWeatherDataByLatLon(result.latitude.toString(),result.longitude.toString(), API_KEY)?.enqueue(object :
            Callback<WeatherData?> {
            override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                if (response.isSuccessful) {
                    val weatherData: WeatherData = response.body()!!
                    mainActivity.findViewById<TextView>(R.id.location).text=weatherData.name
                    mainActivity.findViewById<TextView>(R.id.temperature).text=weatherData.temperature
                    mainActivity.findViewById<TextView>(R.id.description).text=weatherData.weatherDescription
                }
            }
            override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                println(t)
            }
        })
    }
}