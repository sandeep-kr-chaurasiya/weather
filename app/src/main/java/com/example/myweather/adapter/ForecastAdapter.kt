package com.example.myweather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myweather.databinding.RowForecastBinding
import com.example.myweather.weather_data.Forecastday
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastAdapter(private val context: Context, private val forecastList: List<Forecastday>): RecyclerView.Adapter<ForecastAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: RowForecastBinding = RowForecastBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowForecastBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val forecast = forecastList[position]
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = inputFormat.parse(forecast.date)
        val dayName = date?.let { dayFormat.format(it) }
        holder.binding.day.text = dayName
        Glide.with(context)
            .load("https:${forecast.day.condition.icon}")
            .into(holder.binding.weatherIcon)
        holder.binding.temp.text = "${forecast.day.maxtemp_c}° / ${forecast.day.mintemp_c}°C"
        holder.binding.rain.text = "Rain : ${forecast.hour[position].precip_mm} mm"
        holder.binding.condition.text = forecast.day.condition.text
    }

}