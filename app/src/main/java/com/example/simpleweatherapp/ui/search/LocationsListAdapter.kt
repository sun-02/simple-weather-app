package com.example.simpleweatherapp.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.databinding.ItemLocationBinding
import com.example.simpleweatherapp.model.Location

class LocationsListAdapter(
    private val itemClickListener: OnItemClickListener
) : ListAdapter<Location, LocationsListAdapter.LocationViewHolder>(DiffCallback) {

    class LocationViewHolder(
        private var binding: ItemLocationBinding,
        private val itemClickListener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        fun bind(location: Location) {
            binding.namedLocation.text = location.name
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.onClick(v, adapterPosition)
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

    companion object DiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location,
                                     newItem: Location): Boolean {
            return oldItem.name == newItem.name

        }

        override fun areContentsTheSame(oldItem: Location,
                                        newItem: Location): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnItemClickListener {
    fun onClick(view: View?, position: Int)
}