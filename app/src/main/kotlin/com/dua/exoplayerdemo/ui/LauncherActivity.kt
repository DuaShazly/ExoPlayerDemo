package com.dua.exoplayerdemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.dua.exoplayerdemo.ui.video.activity.VideoSelectionActivity
import com.dua.exoplayerdemo.R
import com.dua.exoplayerdemo.adapter.LauncherListAdapter
import com.dua.exoplayerdemo.ui.audio.activity.AudioSelectionActivity
import com.dua.exoplayerdemo.ui.video.activity.VideoPlayerActivity_2

class LauncherActivity : AppCompatActivity(), AdapterView.OnItemClickListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)

        val exampleList = findViewById<ListView>(R.id.startup_activity_list)
        exampleList.adapter = LauncherListAdapter(this)
        exampleList.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (position) {
            LauncherListAdapter.INDEX_VIDEO_PLAYBACK -> showVideoSelectionActivity()

            LauncherListAdapter.INDEX_AUDIO_PLAYBACK -> showAudioSelectionActivity()

            LauncherListAdapter.INDEX_VIDEO_ACTIVITY -> showVideoPlayerActivity()
        }
    }

    private fun showVideoSelectionActivity() {
        startActivity(Intent(this, VideoSelectionActivity::class.java))
    }

    private fun showAudioSelectionActivity() {
        startActivity(Intent(this, AudioSelectionActivity::class.java))
    }

    private fun showVideoPlayerActivity() {
        startActivity(Intent(this, VideoPlayerActivity_2::class.java))
    }
}