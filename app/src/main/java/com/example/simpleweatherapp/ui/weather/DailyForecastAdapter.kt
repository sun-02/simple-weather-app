package com.example.simpleweatherapp.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.DateTimeFormatters.dateFormatter
import com.example.simpleweatherapp.DateTimeFormatters.dowFormatter
import com.example.simpleweatherapp.databinding.ItemDailyForecastBinding
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.*
import com.example.simpleweatherapp.ResourcesMapping.weatherIconsRes
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DailyForecastAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<DailyForecast, DailyForecastAdapter.DailyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {

        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.instant == newItem.instant
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyForecastViewHolder =
        DailyForecastViewHolder(
            ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )


    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val dailyForecast = getItem(position)
        holder.bind(dailyForecast)
    }

    class DailyForecastViewHolder(
        private val binding: ItemDailyForecastBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(df: DailyForecast) {
            val redColorRed = ContextCompat.getColor(itemView.context, R.color.text_color_red)
            val textColor = ContextCompat.getColor(itemView.context, R.color.text_color)

            val date = DtUtil.localDateOfInstant(df.instant, df.zoneOffset)
            val dowFormatted = when (layoutPosition) {
                0 -> itemView.context.getString(R.string.today)
                1 -> itemView.context.getString(R.string.tomorrow)
                else -> date.format(dowFormatter)
            }
            val dowSpanned = if(
                date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
            ) {
                paintStartValue(dowFormatted, dowFormatted, redColorRed)
            } else {
                paintStartValue(dowFormatted, dowFormatted, textColor)
            }

            val dateFormatted = date.format(dateFormatter)

            val weatherIconRes =
                weatherIconsRes[df.weatherIcon] ?: R.drawable.ic_unavailable

            val tempDay = df.dayTemp
            val tempDayFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempDay))

            val tempNight = df.nightTemp
            val tempNightFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempNight))

            binding.apply {
                tvDailyDow.text = dowSpanned
                tvDailyDate.text = dateFormatted
                ivDailyWeather.setImageResource(weatherIconRes)
                tvDailyTempDay.text = tempDayFormatted
                tvDailyTempNight.text = tempNightFormatted
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v, layoutPosition)
        }
    }
}