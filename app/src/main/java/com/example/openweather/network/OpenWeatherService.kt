package com.example.openweather.network

import com.example.openweather.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("weather")
    fun getCurrentWeatherDataByCityName(
        @Query("q") location: String?,
        @Query("appid") apiKey: String?
    ): Call<WeatherData?>?
    @GET("weather")
    fun getCurrentWeatherDataByLatLon(
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?,
        @Query("appid") apiKey: String?
    ): Call<WeatherData?>?
}