package com.example.openweather.model

class WeatherData {
    val name: String? = null
    val temperature: String? = null
    val weatherDescription: String? = null
    val icon: String? = null
}

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