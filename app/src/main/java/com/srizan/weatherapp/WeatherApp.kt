package com.srizan.weatherapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.srizan.weatherapp.utils.WORKER_NAME
import com.srizan.weatherapp.utils.WORKER_TAG
import com.srizan.weatherapp.worker.WeatherNotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        //Get worker instance
        val workManager = WorkManager.getInstance(applicationContext)
        //Get the worker info
        val workInfo = workManager.getWorkInfosByTag(WORKER_TAG).get()
        //Check if the worker is already scheduled
        if (!isWorkScheduled(workInfo)) {
            //Create Worker Constraints
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            //Create the work request with time interval
            val repeatingRequest =
                PeriodicWorkRequestBuilder<WeatherNotificationWorker>(4, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .addTag(WORKER_TAG)
                    .build()

            //Handover the worker to the system
            workManager.enqueueUniquePeriodicWork(
                WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }

    private fun isWorkScheduled(workInfo: List<WorkInfo>?): Boolean {
        var workerIsActive = false
        if (workInfo == null || workInfo.isEmpty()) return false

        for (workStatus in workInfo) {
            workerIsActive =
                workStatus.state == WorkInfo.State.RUNNING || workStatus.state == WorkInfo.State.ENQUEUED
        }
        return workerIsActive
    }

    //Custom worker configuration to use hilt in Worker class
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}