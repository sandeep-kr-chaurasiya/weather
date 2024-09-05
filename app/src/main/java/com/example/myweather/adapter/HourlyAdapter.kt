package com.example.myweather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.databinding.RowHourlyBinding
import com.example.myweather.weather_data.Hour
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyAdapter(private val data: List<Hour>) : RecyclerView.Adapter<HourlyAdapter.UserViewHolder>() {


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: RowHourlyBinding = RowHourlyBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowHourlyBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = data[position]
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh a", Locale.getDefault())
        try {
            val date = inputFormat.parse(user.time)
            val formattedTime = date?.let { outputFormat.format(it) } ?: user.time.substring(11, 16)
            holder.binding.time.text = formattedTime
        } catch (e: ParseException) {
            holder.binding.time.text = user.time.substring(11, 16)
        }
        holder.binding.temp.text = "${user.temp_c} ℃"
        holder.binding.rain.text = "${user.precip_mm} mm"
    }
}
