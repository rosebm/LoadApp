package com.rosalynbm

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.rosalynbm.MainActivity.Companion.CHANNEL_ID
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        main_download_button.setOnClickListener {
            if (url != "")
                download(url)
            else
                Toast.makeText(this, "Must select one option", Toast.LENGTH_SHORT).show()
        }

        main_download_options.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.main_radio_glide ->
                    url = "https://github.com/bumptech/glide"

                R.id.main_radio_udacity ->
                    url = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"

                R.id.main_radio_retrofit ->
                    url = "https://github.com/square/retrofit"
            }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
       //ros val channelId = BuildConfig.CHANNEL_ID
        val channelId = CHANNEL_ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_download_icon)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Since android Oreo, notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Channel readable title", NotificationManager.IMPORTANCE_HIGH)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        var finishDownload = false
        var progress = 0

        while (!finishDownload) {
            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

            if (cursor.moveToFirst()) {
                val status =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                when (status) {
                    DownloadManager.STATUS_FAILED -> finishDownload = true
                    DownloadManager.STATUS_PAUSED -> {}
                    DownloadManager.STATUS_PENDING -> {}
                    DownloadManager.STATUS_RUNNING -> {
                        val total: Long = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total >= 0) {
                            val downloaded =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = ((downloaded * 100L) / total).toInt()
                            // if you use downloadmanger in async task, here you can use like this to display progress.
                            // Don't forget to do the division in long to get more digits rather than double.
                            //  publishProgress((int) ((downloaded * 100L) / total));
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100;
                        // if you use aysnc task
                        // publishProgress(100);
                        finishDownload = true;
                        Toast.makeText(this, "Download Completed", Toast.LENGTH_SHORT).show()

                        sendNotification("LoadApp", "Your file downloaded successfully")
                    }
                }
            }
        }

    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "LoadApp"
    }

}
