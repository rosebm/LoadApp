package com.rosalynbm.ui.main

import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.rosalynbm.BuildConfig
import com.rosalynbm.R
import com.rosalynbm.utils.Const.FILE_NAME
import com.rosalynbm.utils.Const.URL
import com.rosalynbm.utils.NotificationUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), LoadingButtonListener {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url: String = ""
    private var fileName: String = ""
    private lateinit var objectAnimator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // init timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        //registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        main_download_button.setLoadingButtonListener(this)
        main_download_button.setOnClickListener {
            if (url != "") {
                main_download_button.startAnimation()
            }
            else
                Toast.makeText(this, getString(R.string.main_toast_text), Toast.LENGTH_SHORT).show()
        }

        main_download_options.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.main_radio_glide -> {
                    url = "https://github.com/bumptech/glide"
                    fileName = getString(R.string.glide_file_name)
                }

                R.id.main_radio_udacity -> {
                    url = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    fileName = getString(R.string.udacity_file_name)
                }

                R.id.main_radio_retrofit -> {
                    url = "https://github.com/square/retrofit"
                    fileName = getString(R.string.retrofit_file_name)
                }
            }
        }
    }


    /*private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }*/

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
                        val byteTotal: Double = cursor.getDouble(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        if (byteTotal >= 0) {
                            val bytesDownloaded =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            Timber.d("ROS downloaded $bytesDownloaded ")
                            progress = ((bytesDownloaded * 100L) / byteTotal).toInt()
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Timber.d("ROS completed ")
                        // if you use aysnc task
                        finishDownload = true
                        //Toast.makeText(this, "Download Completed", Toast.LENGTH_SHORT).show()

                        NotificationUtil(this)
                            .sendNotification(mapOf(URL to url), mapOf(FILE_NAME to fileName))

                        main_download_button.downloadCompleted()
                    }
                }
            }
        }

    }

    override fun finished() {
        download(url)
    }

}

interface LoadingButtonListener {
    fun finished()
}
