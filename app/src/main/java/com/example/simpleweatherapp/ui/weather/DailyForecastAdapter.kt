package com.example.simpleweatherapp.ui.weather

import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.databinding.ItemDailyForecastBinding
import com.example.simpleweatherapp.model.openweather.DailyForecast
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.getResFromRange


class DailyForecastAdapter(
    private val weatherIconsRes: ArrayMap<String, List<Int>>,
    private val uviIconsRes: ArrayMap<ClosedRange<Double>, Int>,
    private val windDirections: ArrayMap<ClosedRange<Int>, String>,
    private val listener: OnItemClickListener
) : ListAdapter<DailyForecast,
        DailyForecastAdapter.DailyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {

        private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d")
        private val dowFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEEE", Locale("en", "US"))

        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }

    class DailyForecastViewHolder(
        private val binding: ItemDailyForecastBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(df: DailyForecast,
                 weatherIconRes: Int,
                 windDegText: String,
                 windArrow: Drawable,
                 uviIcon: Drawable,
                 groupVisibility: Int,
        ) {
            binding.apply {
                tvDow.text = df.date.format(dowFormatter)
                tvDate.text = df.date.format(dateFormatter)
                ivWeather.setImageResource(weatherIconRes)
                tvTempDay.text = df.dayTemp.toString()
                tvTempNight.text = df.nightTemp.toString()
                tvWind.text = df.windSpeed.toString()
                tvWindDeg.text = windDegText
                tvWindDeg.setCompoundDrawables(windArrow, null, null, null)
                tvHumidity.text = df.humidity.toString()
                tvPressure.text = df.pressure.toString()
                tvUvi.setCompoundDrawables(null, uviIcon, null, null)
                tvUvi.text = df.uvi.toString()
                expandableGroup.visibility = groupVisibility
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v, layoutPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyForecastViewHolder {
        return DailyForecastViewHolder(
            ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context)),
            listener
        )
    }

    override fun onBindViewHolder(
        holder: DailyForecastViewHolder,
        position: Int
    ) {
        val weather = getItem(position)
        val weatherIconRes =
            weatherIconsRes[weather.weatherIcon]?.get(1) ?: R.drawable.ic_unavailable
        val windDirText = getResFromRange(windDirections, weather.windDeg) ?: "N/A"
        val windArrow = RotateDrawable().apply {
            drawable = AppCompatResources.getDrawable(
                holder.itemView.context, R.drawable.ic_arrow_16dp)
            pivotX = 0.5f
            pivotY = 0.5f
            fromDegrees = weather.windDeg.toFloat()
        }
        val uviIconRes = getResFromRange(uviIconsRes, weather.uvi) ?: R.drawable.ic_unavailable
        val uviIcon = AppCompatResources.getDrawable(holder.itemView.context, uviIconRes)
        val groupVisibility = if (weather.expanded) {
            View.VISIBLE
        } else {
            View.GONE
        }
        holder.bind(weather, weatherIconRes, windDirText, windArrow, uviIcon!!, groupVisibility)
    }
}
