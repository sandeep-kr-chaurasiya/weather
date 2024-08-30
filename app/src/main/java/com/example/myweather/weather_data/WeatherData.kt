package com.example.myweather.weather_data

data class WeatherData(
    val alerts: Alerts,
    val current: Current,
    val forecast: Forecast,
    val location: Location
)