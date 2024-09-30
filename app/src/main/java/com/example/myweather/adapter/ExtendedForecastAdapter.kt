package com.example.myweather.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.databinding.RowExtendedForecastBinding
import com.example.myweather.weather_data.Forecastday
import java.text.SimpleDateFormat
import java.util.Locale

class ExtendedForecastAdapter(private val context: Context, private val forecastList: List<Forecastday>) : RecyclerView.Adapter<ExtendedForecastAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: RowExtendedForecastBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowExtendedForecastBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val forecast = forecastList[position]

        holder.binding.day.text = formatDate(forecast.date)

        Glide.with(context)
            .load("https:${forecast.day.condition.icon}")
            .into(holder.binding.weatherIcon)
        holder.binding.temperature.text = "${forecast.day.maxtemp_c}°C"
        holder.binding.feelsLike.text = "Feels like ${forecast.day.maxtemp_c}°C"
        holder.binding.wind.text = "${forecast.day.maxwind_kph} km/h"
        holder.binding.humidity.text = "${forecast.day.avghumidity}%"
        holder.binding.precipitation.text = "${forecast.day.totalprecip_mm} mm"
        holder.binding.uvIndex.text = "${forecast.day.uv}"
        holder.binding.minMaxTemp.text = "${forecast.day.mintemp_c}°C | ${forecast.day.maxtemp_c}°C"
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return date?.let { dayFormat.format(it) } ?: dateString
    }
}
