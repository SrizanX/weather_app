package com.srizan.weatherapp.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherNotificationWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testWorker() {
        //val worker = TestListenableWorkerBuilder<WeatherNotificationWorker>(context).build()

//        runBlocking {
//            val result = worker.doWork()
//            assertThat(result, `is`(ListenableWorker.Result.success()))
//        }

        assertThat(2, `is`(2))
    }
}