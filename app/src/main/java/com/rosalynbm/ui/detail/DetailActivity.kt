package com.rosalynbm.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rosalynbm.R
import com.rosalynbm.utils.Const.FILE_NAME
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.coroutines.*

class DetailActivity : AppCompatActivity(), View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        detail_button.setOnClickListener(this)

        detail_file_name.text = intent.extras?.getString(FILE_NAME)
        detail_status_name.text = "Success"
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch (Dispatchers.IO){
            delay(1000)
            withContext(Dispatchers.Main){
                detail_motion_layout.transitionToEnd()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.detail_button -> finish()
        }
    }

}
