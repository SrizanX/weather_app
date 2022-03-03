package com.srizan.weatherapp.network

import com.srizan.weatherapp.model.City
import com.srizan.weatherapp.model.CityListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/find?lat=23.68&lon=90.35&cnt=50&appid=e384f9ac095b2109c751d95296f8ea76&units=metric")
    suspend fun getCityList(): CityListResponse

    @GET("data/2.5/weather?appid=e384f9ac095b2109c751d95296f8ea76&units=metric")
    suspend fun getCurrentTemperature(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): City
}