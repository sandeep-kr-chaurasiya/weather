package com.example.myweather.api

import com.example.myweather.weather_data.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast.json")
    fun getWeatherWithLocation(
        @Query("key") key: String,
        @Query("q") location: String,
        @Query("days") days: Int = 8,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes"
    ): Call<WeatherData>
//  days=3&aqi=yes&alerts=yes
//  https://api.weatherapi.com/v1/forecast.json?key=47c045fba36a4a9b9d8182320241808&q=18.7988226,84.1329493%20&days=3&aqi=yes&alerts=yes
}