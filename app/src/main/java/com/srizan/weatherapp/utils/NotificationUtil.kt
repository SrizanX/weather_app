package com.srizan.weatherapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.srizan.weatherapp.R
import com.srizan.weatherapp.model.City
import com.srizan.weatherapp.ui.details.WeatherDetailsFragmentArgs


fun sendNotification(text: String, context: Context, data: City) {

    // Create notification channel if the os version is Android Oreo or above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(
            CHANNEL_ID, name, importance
        ).apply {
            enableLights(true)
            enableVibration(true)
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }


    // Create bitmap of the Large Icon
    val bigPicture = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.cloud
    )
    //Arguments to send over to Weather details fragment using Deep Link
    val args = WeatherDetailsFragmentArgs(data).toBundle()

    val pendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.weatherDetailsFragment)
        .setArguments(args)
        .createPendingIntent()

    // Build the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.cloud)
        .setLargeIcon(bigPicture)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)


    //Notify
    val manager = NotificationManagerCompat.from(context)
    manager.cancelAll()
    manager.notify(NOTIFICATION_ID, builder.build())

}