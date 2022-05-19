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
import com.example.simpleweatherapp.util.ResourcesMapping.weatherIconsRes
import java.time.DayOfWeek


class DailyForecastAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<DailyForecast, DailyForecastAdapter.DailyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {

        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
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


    override fun onBindViewHolder(
        holder: DailyForecastViewHolder,
        position: Int
    ) {
        val dailyForecast = getItem(position)
        holder.bind(dailyForecast)
    }

    class DailyForecastViewHolder(
        private val binding: ItemDailyForecastBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(
            df: DailyForecast
        ) {
            val redColorRed = ContextCompat.getColor(itemView.context, R.color.text_color_red)
            val textColor = ContextCompat.getColor(itemView.context, R.color.text_color)

            val dowFormatted = when (layoutPosition) {
                0 -> itemView.context.getString(R.string.today)
                1 -> itemView.context.getString(R.string.tomorrow)
                else -> df.date.format(dowFormatter)
            }
            val dowSpanned = if(
                df.date.dayOfWeek == DayOfWeek.SATURDAY || df.date.dayOfWeek == DayOfWeek.SUNDAY
            ) {
                paintStartValue(dowFormatted, dowFormatted, redColorRed)
            } else {
                paintStartValue(dowFormatted, dowFormatted, textColor)
            }

            val dateFormatted = df.date.format(dateFormatter)

            val weatherIconRes =
                weatherIconsRes[df.weatherIcon] ?: R.drawable.ic_unavailable

            val tempDay = df.dayTemp.toInt()
            val tempDayFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempDay))

            val tempNight = df.nightTemp.toInt()
            val tempNightFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempNight))

            binding.apply {
                tvDow.text = dowSpanned
                tvDate.text = dateFormatted
                ivWeather.setImageResource(weatherIconRes)
                tvTempDay.text = tempDayFormatted
                tvTempNight.text = tempNightFormatted
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v, layoutPosition)
        }
    }
}