package com.aki.realestatemanagerv2

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        //DEFINE WORK HERE (notification for added estate)
            sendVisualNotification(
                "The new estate has been added successfully.",
                title = "Real Estate Manager"
            )
        return Result.success()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendVisualNotification(
        messageBody: String,
        title: String
    ) {
        // Creating an Intent that will be shown when user will click on the notification
        val intent = Intent(this.applicationContext, ItemDetailHostActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // Creating a style for the notification
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(messageBody)

        // Creating a channel (for android 8 and up)
        val channelId = "MAIN NOTIFICATIONS"

        // Building a notification Object
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle)

        // Adding the notification to the notification manager and showing it
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = "Main notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Showing notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_TAG = "REALESTATE"
        var notificationsEnabled = false
    }
}