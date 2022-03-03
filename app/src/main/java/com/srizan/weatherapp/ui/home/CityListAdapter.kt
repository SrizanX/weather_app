package com.srizan.weatherapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.srizan.weatherapp.databinding.ItemCityListBinding
import com.srizan.weatherapp.model.City

class CityListAdapter : ListAdapter<City, CityListAdapter.CityViewHolder>(CityDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {

        return CityViewHolder(
            ItemCityListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class CityViewHolder(
        private val binding: ItemCityListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setCityItemClickListener { view ->
                binding.city?.let { city ->
                    navigateToDetails(city, view)
                }
            }
        }


        private fun navigateToDetails(city: City, view: View?) {
            val action = CityListFragmentDirections.actionCityListFragmentToWeatherDetailsFragment(city)
            view?.findNavController()?.navigate(action)
        }

        fun bind(newCity: City) {
            binding.apply {
                city = newCity
            }
        }
    }
}


class CityDiffCallback : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }

}

