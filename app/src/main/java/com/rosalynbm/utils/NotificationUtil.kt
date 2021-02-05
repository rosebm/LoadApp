package com.rosalynbm.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rosalynbm.R
import com.rosalynbm.ui.detail.DetailActivity

class NotificationUtil(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "LoadApp"
    }

    fun sendNotification(title: String?, messageBody: String?, url: Map<String, String>,
                         fileName: Map<String, String>) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("url", url.getValue("url"))
        intent.putExtra("file_name", fileName.getValue("file_name"))

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        //ros val channelId = BuildConfig.CHANNEL_ID
        val channelId = CHANNEL_ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_download_icon)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Since android Oreo, notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Channel readable title", NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }

        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0
        val notificationId = 0

        NotificationManagerCompat.from(context).apply {
            // Issue the initial notification with zero progress
            notificationBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
            notify(notificationId, notificationBuilder.build())

            // When done, update the notification one more time to remove the progress bar
            notificationBuilder.setContentText(messageBody)
                .setProgress(0,0, false)
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }
}