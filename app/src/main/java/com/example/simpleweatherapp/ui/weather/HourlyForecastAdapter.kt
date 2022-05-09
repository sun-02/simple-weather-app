package com.example.simpleweatherapp.ui.weather

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.ItemHourlyForecastBinding
import com.example.simpleweatherapp.model.openweather.HourlyForecast
import java.time.format.DateTimeFormatter

class HourlyForecastAdapter(
    private val weatherIconResources: ArrayMap<String, List<Int>>
) : ListAdapter<HourlyForecast, 
        HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<HourlyForecast>() {

        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

        override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem.dateEpoch == newItem.dateEpoch
        }

        override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem == newItem
        }
    }
    
    class HourlyForecastViewHolder(
        private val binding: ItemHourlyForecastBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hourlyForecast: HourlyForecast, weatherIconRes: Int) {
            binding.apply {
                tvHour.text = hourlyForecast.time.format(formatter)
                ivWeather.setImageResource(weatherIconRes)
                tvTemp.text = hourlyForecast.temp.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HourlyForecastViewHolder {
        return HourlyForecastViewHolder(
            ItemHourlyForecastBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(
        holder: HourlyForecastViewHolder,
        position: Int
    ) {
        val weather = getItem(position)
        val weatherIconRes =
            weatherIconResources[weather.weatherIcon]?.get(1) ?: R.drawable.ic_unavailable 
        holder.bind(weather, weatherIconRes)
    }
}
