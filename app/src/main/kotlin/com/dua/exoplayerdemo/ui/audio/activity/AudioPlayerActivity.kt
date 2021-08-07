package com.dua.exoplayerdemo.ui.audio.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.devbrackets.android.exomedia.util.millisToFormattedTimeString
import com.devbrackets.android.playlistcore.data.MediaProgress
import com.devbrackets.android.playlistcore.data.PlaybackState
import com.devbrackets.android.playlistcore.listener.PlaylistListener
import com.devbrackets.android.playlistcore.listener.ProgressListener
import com.dua.exoplayerdemo.R
import com.dua.exoplayerdemo.app.ExoPlayerApp
import com.dua.exoplayerdemo.data.MediaItem
import com.dua.exoplayerdemo.data.Samples
import com.dua.exoplayerdemo.manager.PlaylistManager
import kotlinx.android.synthetic.main.audio_player_activity.*


class AudioPlayerActivity : AppCompatActivity(), PlaylistListener<MediaItem>, ProgressListener {
    companion object {
        const val EXTRA_INDEX = "EXTRA_INDEX"
        const val PLAYLIST_ID = 4 //Arbitrary, for the example
    }

    private var shouldSetDuration: Boolean = false
    private var userInteracting: Boolean = false

    private lateinit var playlistManager: PlaylistManager
    private val selectedPosition by lazy { intent.extras?.getInt(EXTRA_INDEX, 0) ?: 0 }

    private val glide: RequestManager by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player_activity)
        init()
    }

    override fun onPause() {
        super.onPause()
        playlistManager.unRegisterPlaylistListener(this)
        playlistManager.unRegisterProgressListener(this)
    }

    override fun onResume() {
        super.onResume()
        playlistManager = (applicationContext as ExoPlayerApp).playlistManager
        playlistManager.registerPlaylistListener(this)
        playlistManager.registerProgressListener(this)

        //Makes sure to retrieve the current playback information
        updateCurrentPlaybackInformation()
    }

    override fun onPlaylistItemChanged(currentItem: MediaItem?, hasNext: Boolean, hasPrevious: Boolean): Boolean {
        shouldSetDuration = true

        //Updates the button states
        nextButton.isEnabled = hasNext
        previousButton.isEnabled = hasPrevious

        //Loads the new image
        currentItem?.let {
            glide.load(it.artworkUrl).into(artworkView!!)
        }

        return true
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState): Boolean {
        when (playbackState) {
            PlaybackState.STOPPED -> finish()
            PlaybackState.RETRIEVING, PlaybackState.PREPARING, PlaybackState.SEEKING -> restartLoading()
            PlaybackState.PLAYING -> doneLoading(true)
            PlaybackState.PAUSED -> doneLoading(false)
            else -> {}
        }

        return true
    }

    override fun onProgressUpdated(mediaProgress: MediaProgress): Boolean {
        if (shouldSetDuration && mediaProgress.duration > 0) {
            shouldSetDuration = false
            setDuration(mediaProgress.duration)
        }

        if (!userInteracting) {
            seekBar.secondaryProgress = (mediaProgress.duration * mediaProgress.bufferPercentFloat).toInt()
            seekBar.progress = mediaProgress.position.toInt()
            currentPositionView.text = mediaProgress.position.millisToFormattedTimeString()
        }

        return true
    }

    /**
     * Makes sure to update the UI to the current playback item.
     */
    private fun updateCurrentPlaybackInformation() {
        playlistManager.currentItemChange?.let {
            onPlaylistItemChanged(it.currentItem, it.hasNext, it.hasPrevious)
        }

        if (playlistManager.currentPlaybackState != PlaybackState.STOPPED) {
            onPlaybackStateChanged(playlistManager.currentPlaybackState)
        }

        playlistManager.currentProgress?.let {
            onProgressUpdated(it)
        }
    }

    private fun init() {
        setupListeners()
        startPlayback(setupPlaylistManager())
    }



    private fun doneLoading(isPlaying: Boolean) {
        loadCompleted()
        updatePlayPauseImage(isPlaying)
    }


    private fun updatePlayPauseImage(isPlaying: Boolean) {
        val resId = if (isPlaying) R.drawable.playlistcore_ic_pause_black else R.drawable.playlistcore_ic_play_arrow_black
        playPauseButton.setImageResource(resId)
    }

    private fun loadCompleted() {
        playPauseButton.visibility = View.VISIBLE
        previousButton.visibility = View.VISIBLE
        nextButton.visibility = View.VISIBLE

        loadingBar.visibility = View.INVISIBLE
    }


    private fun restartLoading() {
        playPauseButton.visibility = View.INVISIBLE
        previousButton.visibility = View.INVISIBLE
        nextButton.visibility = View.INVISIBLE

        loadingBar.visibility = View.VISIBLE
    }


    private fun setDuration(duration: Long) {
        seekBar.max = duration.toInt()
        durationView.text = duration.millisToFormattedTimeString()
    }


    private fun setupPlaylistManager(): Boolean {
        playlistManager = (applicationContext as ExoPlayerApp).playlistManager

        //There is nothing to do if the currently playing values are the same
        if (playlistManager.id == PLAYLIST_ID.toLong()) {
            return false
        }

        val mediaItems = Samples.getAudioSamples().map {
            MediaItem(it, true)
        }

        playlistManager.setParameters(mediaItems, selectedPosition)
        playlistManager.id = PLAYLIST_ID.toLong()

        return true
    }

    private fun setupListeners() {
        seekBar.setOnSeekBarChangeListener(SeekBarChanged())
        previousButton.setOnClickListener { playlistManager.invokePrevious() }
        playPauseButton.setOnClickListener { playlistManager.invokePausePlay() }
        nextButton.setOnClickListener { playlistManager.invokeNext() }
    }


    private fun startPlayback(forceStart: Boolean) {
        //If we are changing audio files, or we haven't played before then start the playback
        if (forceStart || playlistManager.currentPosition != selectedPosition) {
            playlistManager.currentPosition = selectedPosition
            playlistManager.play(0, false)
        }
    }


    private inner class SeekBarChanged : SeekBar.OnSeekBarChangeListener {
        private var seekPosition = -1

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!fromUser) {
                return
            }

            seekPosition = progress
            currentPositionView.text = progress.toLong().millisToFormattedTimeString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            userInteracting = true

            seekPosition = seekBar.progress
            playlistManager.invokeSeekStarted()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            userInteracting = false

            playlistManager.invokeSeekEnded(seekPosition.toLong())
            seekPosition = -1
        }
    }
}