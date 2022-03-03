package com.srizan.weatherapp.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.srizan.weatherapp.model.City
import com.srizan.weatherapp.network.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherDetailsViewModel
@Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    val city = MutableLiveData<City>()
}