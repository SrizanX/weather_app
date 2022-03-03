package com.srizan.weatherapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srizan.weatherapp.model.City
import com.srizan.weatherapp.network.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CityListViewModel
@Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val cityList = MutableLiveData<ArrayList<City>>()
    val isLoading = MutableLiveData(false)
    val errorHappened = MutableLiveData(false)

    init {
        getCityList()
    }

    fun getCityList() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                cityList.value = weatherRepository.getCityList().list
                errorHappened.value = false
                isLoading.value = false
            } catch (e: Exception) {
                isLoading.value = false
                errorHappened.value = true
                e.stackTrace
            }
        }
    }
}