package com.example.myweather.weather_data

data class AirQuality(
    val co: Double,
    val gb_defra_index: Int,
    val no2: Double,
    val o3: Double,
    val pm10: Double,
    val pm25: Double,
    val so2: Double,
    val us_epa_index: Int
)