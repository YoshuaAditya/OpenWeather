package com.example.openweather.model

data class WeatherData (
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
)

data class Coord(
    val lon: Double,
    val lat: Double,
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String,
)

data class Main(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long,
    val seaLevel: Long,
    val grndLevel: Long,
)

data class Wind(
    val speed: Double,
    val deg: Long,
    val gust: Double,
)

data class Clouds(
    val all: Long,
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)
/*
{
  "coord": {
    "lon": 112.6304,
    "lat": -7.9797
  },
  "weather": [
    {
      "id": 804,
      "main": "Clouds",
      "description": "overcast clouds",
      "icon": "04d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 299.95,
    "feels_like": 300.67,
    "temp_min": 299.95,
    "temp_max": 299.95,
    "pressure": 1012,
    "humidity": 55,
    "sea_level": 1012,
    "grnd_level": 961
  },
  "visibility": 10000,
  "wind": {
    "speed": 1.91,
    "deg": 199,
    "gust": 1.96
  },
  "clouds": {
    "all": 96
  },
  "dt": 1690448072,
  "sys": {
    "country": "ID",
    "sunrise": 1690411415,
    "sunset": 1690453688
  },
  "timezone": 25200,
  "id": 1636722,
  "name": "Malang",
  "cod": 200
}

{
  "coord": {
    "lon": -10.5932,
    "lat": 6.6518
  },
  "weather": [
    {
      "id": 804,
      "main": "Clouds",
      "description": "overcast clouds",
      "icon": "04d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 296.83,
    "feels_like": 297.71,
    "temp_min": 296.83,
    "temp_max": 296.83,
    "pressure": 1016,
    "humidity": 94,
    "sea_level": 1016,
    "grnd_level": 1007
  },
  "visibility": 10000,
  "wind": {
    "speed": 0.18,
    "deg": 61,
    "gust": 0.21
  },
  "clouds": {
    "all": 91
  },
  "dt": 1690445320,
  "sys": {
    "country": "LR",
    "sunrise": 1690439752,
    "sunset": 1690484499
  },
  "timezone": 0,
  "id": 2274890,
  "name": "New",
  "cod": 200
}
*/