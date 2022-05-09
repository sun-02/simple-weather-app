package com.example.simpleweatherapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.databinding.ItemLocationBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.ui.OnItemClickListener

class LocationsListAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<ShortLocation, LocationsListAdapter.LocationViewHolder>(DiffCallback) {

    class LocationViewHolder(
        private val binding: ItemLocationBinding,
        private val itemClickListener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        fun bind(shortLocation: ShortLocation) {
            binding.namedLocation.text = shortLocation.name
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationViewHolder {
        return LocationViewHolder(ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context)), itemClickListener)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = getItem(position)
        holder.bind(location)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ShortLocation>() {
        override fun areItemsTheSame(oldItem: ShortLocation,
                                     newItem: ShortLocation
        ): Boolean {
            return oldItem.name == newItem.name

        }

        override fun areContentsTheSame(oldItem: ShortLocation,
                                        newItem: ShortLocation
        ): Boolean {
            return oldItem == newItem
        }
    }
}

