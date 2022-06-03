package com.example.simpleweatherapp.ui.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.ItemFavoriteLocationsBinding
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.ResourcesMapping.weatherImagesRes
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.intToSignedString

class FavWeatherAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<ShortWeather, FavWeatherAdapter.FavWeatherViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ShortWeather>() {

        override fun areItemsTheSame(oldItem: ShortWeather, newItem: ShortWeather): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ShortWeather, newItem: ShortWeather): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavWeatherViewHolder =
        FavWeatherViewHolder(
            ItemFavoriteLocationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            ), listener
        )

    override fun onBindViewHolder(holder: FavWeatherViewHolder, position: Int) {
        val shortWeather = getItem(position)
        holder.bind(shortWeather)
    }

    class FavWeatherViewHolder(
        private val binding: ItemFavoriteLocationsBinding,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(sw: ShortWeather) {
            val locName = sw.name.split(", ")[0]
            val weatherRes = weatherImagesRes[sw.weatherIcon] ?: R.drawable.ic_unavailable
            val tempFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(sw.temp))

            binding.apply {
                tvFavLocation.text = locName
                ivFavWeather.setImageResource(weatherRes)
                tvFavTemp.text = tempFormatted
                ivFavRemove.setOnClickListener(this@FavWeatherViewHolder)
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v, layoutPosition)
        }
    }
}
