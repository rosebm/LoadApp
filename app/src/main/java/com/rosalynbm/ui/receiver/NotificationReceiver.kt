package com.rosalynbm.ui.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rosalynbm.ui.detail.DetailActivity
import com.rosalynbm.utils.Const.FILE_NAME
import com.rosalynbm.utils.Const.URL

class NotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras

        val intent = Intent(context, DetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(URL, bundle?.getString(URL))
        intent.putExtra(FILE_NAME, bundle?.getString(FILE_NAME))
        context?.startActivity(intent)

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }
}