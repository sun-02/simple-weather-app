package com.example.simpleweatherapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.DateTimeFormatters.timeFormatter
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.ItemHourlyForecastBinding
import com.example.simpleweatherapp.model.openweather.HourlyForecast
import com.example.simpleweatherapp.ResourcesMapping.weatherIconsRes
import com.example.simpleweatherapp.util.DtUtil
import com.example.simpleweatherapp.util.intToSignedString
import java.time.LocalTime
import java.time.ZoneOffset

class HourlyForecastAdapter : ListAdapter<HourlyForecast,
        HourlyForecastAdapter.HourlyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<HourlyForecast>() {

        override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem.instant == newItem.instant
        }

        override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem == newItem
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
    ) = holder.bind(getItem(position))

    class HourlyForecastViewHolder(
        private val binding: ItemHourlyForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hf: HourlyForecast) {
            val temp = hf.temp
            val tempFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(temp))

            val weatherIconRes =
                weatherIconsRes[hf.weatherIcon] ?: R.drawable.ic_unavailable

            binding.apply {
                tvHour.text = DtUtil.localTimeOfInstant(hf.instant, hf.zoneOffset).format(timeFormatter)
                ivWeather.setImageResource(weatherIconRes)
                tvTemp.text = tempFormatted
            }
        }
    }
}
