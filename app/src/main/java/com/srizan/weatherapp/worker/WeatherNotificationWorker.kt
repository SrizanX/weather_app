package com.srizan.weatherapp.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.srizan.weatherapp.network.WeatherRepository
import com.srizan.weatherapp.utils.sendNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@HiltWorker
class WeatherNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(appContext, params) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun doWork(): Result {

        return try {
            getLastLocation { location ->
                GlobalScope.launch {
                    location?.let {
                        try {
                            //Get the current temperature using the devices current location coordinates
                            val data =
                                weatherRepository.getCurrentTemperature(
                                    location.latitude,
                                    location.longitude
                                )
                            //Notify the user
                            sendNotification(
                                "Current Temperature ${data.main.temp}°c",
                                applicationContext,
                                data
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getLastLocation(block: (Location?) -> Unit) {

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Get the last known location
            //fusedLocationClient.lastLocation.addOnSuccessListener(block)

            //Get the current location for better accuracy
            fusedLocationClient.getCurrentLocation(
                LocationRequest.QUALITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener(block)
        }
    }
}