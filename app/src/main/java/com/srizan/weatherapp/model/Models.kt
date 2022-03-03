package com.srizan.weatherapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CityListResponse(
    val message: String,
    val cod: String,
    val count: Int,
    val list: ArrayList<City>
)

@Parcelize
data class City(
    val id : Int,
    val name : String,
    val coord : Coordinates,
    val main : Temperature,
    val weather : ArrayList<Weather>,
    val wind : Wind,
) : Parcelable

@Parcelize
data class Temperature(
    val temp : Double,
    val temp_min : Double,
    val temp_max : Double,
    val humidity : Int
) : Parcelable

@Parcelize
data class Weather(
    val id: Int,
    val main : String,
    val description : String
) : Parcelable


@Parcelize
data class Coordinates(
    val lat: Double,
    val lon: Double,
) : Parcelable

@Parcelize
data class Wind(
    val speed : Double,
) : Parcelable
