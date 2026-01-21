package com.source.uberclone.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.source.uberclone.R
import com.source.uberclone.models.DriverInfoModel

object Constants {

    const val NOTIFICATION_CHANNEL_ID = "UberClone"
    const val NOTI_TITLE = "title"
    const val NOTI_BODY = "body"
    const val TOKEN_REFERENCE = "Token"
    const val DRIVER_INFO_REFERENCE = "DriverInfo"

    var currentUser: DriverInfoModel? = null

    fun buildWelcomeMessage(): String {
        return "Welcome, ${currentUser?.firstName} ${currentUser?.lastName}"
    }

    fun showNotification(
        context: Context,
        id: Int,
        title: String?,
        body: String?,
        intent: Intent?
    ) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //  Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Uber Clone Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Uber Clone notification channel"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)

            notificationManager.createNotificationChannel(channel)
        }

        // PendingIntent
        val pendingIntent = intent?.let {
            PendingIntent.getActivity(
                context,
                id,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Notification builder
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_car)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_baseline_car
                )
            )
            .setContentTitle(title ?: "Uber Clone")
            .setContentText(body ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        pendingIntent?.let {
            builder.setContentIntent(it)
        }

        notificationManager.notify(id, builder.build())
    }
}
