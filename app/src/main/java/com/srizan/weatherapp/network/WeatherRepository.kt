package com.srizan.weatherapp.network

import javax.inject.Inject

class WeatherRepository
@Inject constructor(
    private val weatherService: WeatherService
) {
    //Get City List for Home Screen
    suspend fun getCityList() = weatherService.getCityList()
    //Get Current temperature from background
    suspend fun getCurrentTemperature(lat: Double, lon: Double) =
        weatherService.getCurrentTemperature(lat, lon)
}