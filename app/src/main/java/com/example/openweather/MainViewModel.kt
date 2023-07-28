package com.example.openweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.openweather.MainViewModel.PreferencesKeys.DESCRIPTION
import com.example.openweather.MainViewModel.PreferencesKeys.ICON
import com.example.openweather.MainViewModel.PreferencesKeys.NAME
import com.example.openweather.MainViewModel.PreferencesKeys.TEMPERATURE
import com.example.openweather.MainViewModel.PreferencesKeys.TIME
import com.example.openweather.model.WeatherData
import com.example.openweather.network.ApiClient
import com.example.openweather.network.OpenWeatherMapService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class MainViewModel(val dataStore: DataStore<Preferences>) : ViewModel() {
    private object PreferencesKeys {
        val TEMPERATURE = doublePreferencesKey("temperature")
        val NAME = stringPreferencesKey("name")
        val DESCRIPTION = stringPreferencesKey("description")
        val ICON = stringPreferencesKey("icon")
        val TIME = stringPreferencesKey("time")
    }

    private val openWeatherMapService: OpenWeatherMapService =
        ApiClient.instance!!.create(OpenWeatherMapService::class.java)

    fun getCurrentLocation(mainActivity: MainActivity) {
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
        } else {
            getWeather(mainActivity)
        }
    }

    @SuppressLint("MissingPermission")
    fun getWeather(mainActivity: MainActivity) = viewModelScope.launch {
        val nameTextView = mainActivity.findViewById<TextView>(R.id.location)
        val timeTextView = mainActivity.findViewById<TextView>(R.id.time)
        val iconImageView = mainActivity.findViewById<ImageView>(R.id.weather_icon)
        val descriptionTextView = mainActivity.findViewById<TextView>(R.id.description)
        val temperatureTextView = mainActivity.findViewById<TextView>(R.id.temperature)

        val temperatureFlow: Double = dataStore.data.map {
            it[TEMPERATURE] ?: 273.0
        }.first()
        var tempText = "${temperatureFlow.toInt() - 273}\u00B0C"
        temperatureTextView.text = tempText
        val nameFlow: String = dataStore.data.map {
            it[NAME] ?: "Location"
        }.first()
        val timeFlow: String = dataStore.data.map {
            it[TIME] ?: "Last Updated 12:00"
        }.first()
        temperatureTextView.text = tempText
        nameTextView.text = nameFlow
        timeTextView.text = timeFlow
        val descriptionFlow: String = dataStore.data.map {
            it[DESCRIPTION] ?: "Unknown weather"
        }.first()
        descriptionTextView.text = descriptionFlow
        val iconFlow: String = dataStore.data.map {
            it[ICON] ?: "50d"
        }.first()
        Glide.with(mainActivity)
            .load("http://openweathermap.org/img/w/$iconFlow.png")
            .into(mainActivity.findViewById(R.id.weather_icon))

        val lm = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder(mainActivity)
                .setMessage("Please enable Location")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    mainActivity.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                    mainActivity.finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            if (!isOnline()) {
                Toast.makeText(mainActivity, "Not connected to internet", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val locationServices = LocationServices.getFusedLocationProviderClient(mainActivity)
            val result = locationServices.lastLocation.await()
            try {
                println(result.latitude)
            } catch (e: java.lang.NullPointerException) {
                /**
                Handle an error when the application treated by exactly this steps:
                1. turn off internet,location,also revoke location permission
                2. then give permission to location
                3. turn on internet
                4. sometimes val result will null, not consistent
                 */
                Toast.makeText(
                    mainActivity,
                    "Failed to get current location, please try again after some time.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("error", "Failed to get current location")
                return@launch
            }

            openWeatherMapService.getCurrentWeatherDataByLatLon(
                result.latitude.toString(),
                result.longitude.toString(),
                API_KEY
            )?.enqueue(object :
                Callback<WeatherData?> {
                override fun onResponse(
                    call: Call<WeatherData?>,
                    response: Response<WeatherData?>
                ) {
                    if (response.isSuccessful) {
                        val weatherData: WeatherData = response.body()!!
                        nameTextView.text = weatherData.name
                        val temperatureText = "${weatherData.main.temp.toInt() - 273}\u00B0C"
                        temperatureTextView.text = temperatureText
                        descriptionTextView.text =
                            weatherData.weather[0].description
                        Glide.with(mainActivity)
                            .load("http://openweathermap.org/img/w/${weatherData.weather[0].icon}.png")
                            .into(iconImageView)
                        val calendar: Calendar = Calendar.getInstance(Locale.getDefault())
                        val timeText =
                            "Last Updated ${calendar.get(Calendar.HOUR_OF_DAY)}:" + String.format(
                                "%02d",
                                calendar.get(Calendar.MINUTE)
                            )
                        timeTextView.text = timeText

                        viewModelScope.launch {
                            dataStore.edit { preference ->
                                preference[TEMPERATURE] = weatherData.main.temp
                                preference[NAME] = weatherData.name
                                preference[DESCRIPTION] = weatherData.weather[0].description
                                preference[ICON] = weatherData.weather[0].icon
                                preference[TIME] = timeText
                                println(preference[TIME])
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                    println(t)
                }
            })
            openWeatherMapService.getCurrentWeatherDataByCityName(
                "Singapore",
                API_KEY
            )?.enqueue(object :
                Callback<WeatherData?> {
                override fun onResponse(
                    call: Call<WeatherData?>,
                    response: Response<WeatherData?>
                ) {
                    if (response.isSuccessful) {
                        val weatherData: WeatherData = response.body()!!
                        val temperatureText = "${weatherData.main.temp.toInt() - 273}\u00B0C"
                        mainActivity.findViewById<TextView>(R.id.weather_singapore).text = temperatureText
                        Glide.with(mainActivity)
                            .load("http://openweathermap.org/img/w/${weatherData.weather[0].icon}.png")
                            .into(mainActivity.findViewById(R.id.weather_icon_singapore))
                    }
                }
                override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                    println(t)
                }
            })
        }
    }

    private fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }
}