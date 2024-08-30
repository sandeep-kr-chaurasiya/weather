package com.example.myweather

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweather.adapter.ExtendedForecastAdapter
import com.example.myweather.databinding.ActivityExtendedForecastBinding
import com.example.myweather.weather_data.Forecast
import com.example.myweather.weather_data.Forecastday
import com.example.myweather.weather_data.WeatherData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExtendedForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExtendedForecastBinding
    private lateinit var extendedForecastAdapter: ExtendedForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExtendedForecastBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.extendedForecastRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("key", null)

        val gson = Gson()
        val type = object : TypeToken<List<Forecastday>>() {}.type
        val forecastData: List<Forecastday> = gson.fromJson(json, type)


        extendedForecastAdapter = ExtendedForecastAdapter(this, forecastData)
        binding.extendedForecastRecyclerView.adapter = extendedForecastAdapter
    }
//    private fun getForecast(weatherData: WeatherData): List<Forecastday> {
//        return weatherData.forecast.forecastday.drop(1)
//    }
}
