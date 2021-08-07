package com.dua.exoplayerdemo.ui.video.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dua.exoplayerdemo.R
import com.potyvideo.library.AndExoPlayerView
import com.potyvideo.library.globalEnums.EnumResizeMode
import com.potyvideo.library.globalInterfaces.AndExoPlayerListener
import com.potyvideo.library.utils.PathUtil
import com.potyvideo.library.utils.PublicFunctions
import com.potyvideo.library.utils.PublicValues


class VideoPlayerActivity_2 : AppCompatActivity(), AndExoPlayerListener, View.OnClickListener {

    lateinit var andExoPlayerView: AndExoPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin)

        andExoPlayerView = findViewById(R.id.andExoPlayerView)

        andExoPlayerView.setResizeMode(EnumResizeMode.FIT) // sync with attrs
        andExoPlayerView.setAndExoPlayerListener(this)

        findViewById<AppCompatButton>(R.id.stream_mp4).setOnClickListener(this)
        findViewById<AppCompatButton>(R.id.stream_hls).setOnClickListener(this)
        findViewById<AppCompatButton>(R.id.stream_dash).setOnClickListener(this)

    }

    override fun onExoPlayerError(errorMessage: String?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.stream_mp4 -> {
                loadMP4Stream(PublicValues.TEST_URL_MP4_V2)
            }
            R.id.stream_hls -> {
                loadHLSStream(PublicValues.TEST_URL_HLS)
            }
            R.id.stream_dash -> {
                loadHLSStream(PublicValues.TEST_URL_DASH)
            }
        }
    }




    private fun loadMP4Stream(urlMP4: String) {
        andExoPlayerView.setSource(urlMP4)
    }

    private fun loadHLSStream(urlHLS: String) {
        andExoPlayerView.setSource(urlHLS)
    }

}