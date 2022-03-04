package com.srizan.weatherapp.ui.details

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.srizan.weatherapp.R
import com.srizan.weatherapp.databinding.WeatherDetailsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class WeatherDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: WeatherDetailsViewModel
    private lateinit var binding: WeatherDetailsFragmentBinding
    private val args: WeatherDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WeatherDetailsFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WeatherDetailsViewModel::class.java)

        //Setup the Map View
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        //Pass the temp data of a city from arguments to view model in order the survive configuration changes
        viewModel.city.value = args.city

        //Observe changes and update the UI
        viewModel.city.observe(viewLifecycleOwner) { city ->
            city?.let {
                binding.apply {
                    textViewCityName.text = city.name
                    textViewStatus.text = city.weather.get(0).main
                    textViewHumidity.text = "Humidity: " + city.main.humidity.toString()
                    textViewWindSpeed.text = "Wind Speed: " + city.wind.speed.toString()
                    textViewMaxTemp.text = "Max Temp: " + city.main.temp_max
                    textViewMinTemp.text = "Min Temp: ${city.main.temp_min}"
                    textViewTempText.text = city.main.temp.roundToInt().toString() + "°c"
                }
            }
        }
        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.map_style_dark
                    )
                )
            } // Night mode is active, we're using dark theme
        }
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)

        val lat = args.city?.coord?.lat ?: 0.0
        val lon = args.city?.coord?.lon ?: 0.0

        val latLng = LatLng(lat, lon)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5f))
        map.addMarker(MarkerOptions().position(latLng).title(args.city?.name))?.showInfoWindow()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}