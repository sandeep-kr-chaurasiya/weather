package com.example.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myweather.adapter.ForecastAdapter
import com.example.myweather.adapter.HourlyAdapter
import com.example.myweather.api.ApiInterface
import com.example.myweather.databinding.ActivityMainBinding
import com.example.myweather.weather_data.Forecastday
import com.example.myweather.weather_data.Hour
import com.example.myweather.weather_data.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import java.util.Calendar
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        dialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            setCancelable(false)
        }
        dialog.show()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.recyclerViewHourly.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewForecast.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocation()
        }

        binding.author.setOnClickListener {

            val webpage: Uri = Uri.parse("https://sandeep-kr-chaurasiya.netlify.app")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
        binding.extendedForecast.setOnClickListener {
            startActivity(Intent(this, ExtendedForecastActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.search)
        val searchView: SearchView? = searchItem?.actionView as? SearchView
        val liveLocation: MenuItem? = menu?.findItem(R.id.live_location)

        liveLocation?.setOnMenuItemClickListener {
            getCurrentLocation()
            true
        }

        searchItem?.setOnMenuItemClickListener {
            searchView?.isIconified = false
            true
        }


        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchWeatherData(it)
                    searchItem.collapseActionView()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                onRequestPermissionsResult(LOCATION_PERMISSION_REQUEST_CODE, permissions, grantResults)
            }
        }
    }

    private fun fetchWeatherData(location: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        val key = "47c045fba36a4a9b9d8182320241808"
        val call = apiInterface.getWeatherWithLocation(key, location)

        call.enqueue(object : Callback<WeatherData> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    dialog.dismiss()
                    val data = response.body()
                    data?.let {
                        // Current weather data
                        binding.apply {
                            currentLocation.text = "${it.location.name}, ${it.location.country}"
                            temperature.text = "${it.current.temp_c}°C"
                            feelsLike.text = "Feels Like: ${it.current.feelslike_c}°C"
                            minMaxTemp.text = "Min ${it.forecast.forecastday.first().day.mintemp_c}°C | Max ${it.forecast.forecastday.first().day.maxtemp_c}°C"
                            precipitation.text = "${it.current.precip_mm} mm"
                            wind.text = "${it.current.wind_kph} km/h"
                            humidity.text = "${it.current.humidity}%"
                            uvIndex.text = it.current.uv.toString()
                            date.text = " ${Calendar.getInstance().time.toString().substring(0, 10)}"
                            val condition = it.current.condition.toString()
                            updateIcon(condition)
                            Log.d("icon", "onResponse:${condition}")
                        }

// ------------------------------ Hourly weather data-------------------------//

                        val currentDayForecast = it.forecast.forecastday.first()
                        val upcomingHourlyData = getHourlyData(currentDayForecast)
                        hourlyAdapter = HourlyAdapter(upcomingHourlyData)
                        binding.recyclerViewHourly.adapter = hourlyAdapter

//                        Forecast weather data
                        val forecastData = getForecast(it) // List<Forecast>
                        forecastAdapter = ForecastAdapter(this@MainActivity, forecastData)
                        binding.recyclerViewForecast.adapter = forecastAdapter

                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        val gson = Gson()
                        val json = gson.toJson(forecastData)
                        editor.putString("key", json)
                        editor.apply()
                    }
                } else {
                    Log.e("WeatherData", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                fetchWeatherData(location)
            }
        })
    }
//--------to add icon -----------//
private fun updateIcon(condition: String) {
    when (condition) {
        "Sunny" -> binding.weatherIcon.setAnimation(R.raw.sunny)
        "Partly cloudy" -> binding.weatherIcon.setAnimation(R.raw.parialy_cloudy)
        "Cloudy" -> binding.weatherIcon.setAnimation(R.raw.cloudy)
        "Overcast" -> binding.weatherIcon.setAnimation(R.raw.overcast)
        "Thundery outbreaks possible" -> binding.weatherIcon.setAnimation(R.raw.thunder)
        "Light rain" -> binding.weatherIcon.setAnimation(R.raw.light_rain)
        "Heavy rain" -> binding.weatherIcon.setAnimation(R.raw.heavy_rain)
        "Moderate or heavy rain with thunder" -> binding.weatherIcon.setAnimation(R.raw.rain_thunder)
        else -> binding.weatherIcon.setAnimation(R.raw.parialy_cloudy)
    }
}
    private fun getHourlyData(forecastDay: Forecastday): List<Hour> {
        return forecastDay.hour
    }

    private fun getForecast(weatherData: WeatherData): List<Forecastday> {
        return weatherData.forecast.forecastday.drop(1)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            val location: Location? = task.result
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val currentLocation = "$latitude,$longitude"
                fetchWeatherData(currentLocation)
            } else {
                getCurrentLocation()
            }
        }
    }

}
