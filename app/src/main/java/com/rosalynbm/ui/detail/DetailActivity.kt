package com.rosalynbm.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rosalynbm.R
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import timber.log.Timber

class DetailActivity : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        detail_button.setOnClickListener(this)

        Timber.d("extra ${intent.extras?.getString("url")}")
        Timber.d("extra ${intent.extras?.getString("file_name")}")

        detail_file_name.text = intent.extras?.getString("file_name")
        detail_status_name.text = "Success"
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.detail_button -> finish()
        }
    }

}
