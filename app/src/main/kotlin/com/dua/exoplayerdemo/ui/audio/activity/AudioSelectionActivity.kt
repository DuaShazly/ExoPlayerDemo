package com.dua.exoplayerdemo.ui.audio.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.dua.exoplayerdemo.R
import com.dua.exoplayerdemo.adapter.SampleListAdapter
import com.dua.exoplayerdemo.data.Samples

/**
 * A simple activity that allows the user to select a
 * chapter form "The Count of Monte Cristo" to play
 * (limited to chapters 1 - 4).
 */
class AudioSelectionActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_selection_activity)

        supportActionBar?.title = resources.getString(R.string.title_audio_selection_activity)

        val exampleList = findViewById<View>(R.id.selection_activity_list) as ListView
        exampleList.adapter = SampleListAdapter(this, Samples.getAudioSamples())
        exampleList.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        startAudioPlayerActivity(position)
    }

    private fun startAudioPlayerActivity(selectedIndex: Int) {
        startActivity(Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.EXTRA_INDEX, selectedIndex)
        })
    }
}