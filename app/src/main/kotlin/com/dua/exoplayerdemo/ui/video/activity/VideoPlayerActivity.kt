package com.dua.exoplayerdemo.ui.video.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageButton
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.devbrackets.android.exomedia.core.renderer.RendererType
import com.devbrackets.android.exomedia.listener.OnVideoSizeChangedListener
import com.devbrackets.android.exomedia.ui.listener.VideoControlsSeekListener
import com.devbrackets.android.exomedia.ui.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.controls.DefaultVideoControls
import com.dua.exoplayerdemo.ui.support.CaptionPopupManager
import com.dua.exoplayerdemo.app.ExoPlayerApp
import com.dua.exoplayerdemo.R
import com.dua.exoplayerdemo.data.MediaItem
import com.dua.exoplayerdemo.data.Samples
import com.dua.exoplayerdemo.manager.PlaylistManager
import com.dua.exoplayerdemo.playlist.VideoApi
import com.dua.exoplayerdemo.ui.support.CaptionPopupManager.Companion.CC_DEFAULT
import com.dua.exoplayerdemo.ui.support.CaptionPopupManager.Companion.CC_DISABLED
import com.dua.exoplayerdemo.ui.support.CaptionPopupManager.Companion.CC_GROUP_INDEX_MOD
import com.dua.exoplayerdemo.ui.support.FullscreenManager
import com.google.android.exoplayer2.util.EventLogger

import kotlinx.android.synthetic.main.video_player_activity.*

open class VideoPlayerActivity : AppCompatActivity(), VideoControlsSeekListener {

    companion object {
        const val EXTRA_INDEX = "EXTRA_INDEX"
        const val PLAYLIST_ID = 6
    }

    private lateinit var videoApi: VideoApi
    private lateinit var playlistManager: PlaylistManager
    private lateinit var captionsButton: AppCompatImageButton

    private val selectedIndex by lazy { intent.extras?.getInt(EXTRA_INDEX, 0) ?: 0 }

    private val captionPopupManager = CaptionPopupManager()

    private val fullscreenManager by lazy {
        FullscreenManager(window) {
            videoView.showControls()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.video_player_activity)

        init()
    }

    override fun onStop() {
        super.onStop()
        if (videoApi.isPlaying) {
            playlistManager.invokeStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playlistManager.removeVideoApi(videoApi)
        playlistManager.invokeStop()
    }

    override fun onSeekStarted(): Boolean {
        playlistManager.invokeSeekStarted()
        return true
    }

    override fun onSeekEnded(seekTime: Long): Boolean {
        playlistManager.invokeSeekEnded(seekTime)
        return true
    }

    private fun init() {
        setupPlaylistManager()

        videoView.handleAudioFocus = false
        videoView.setAnalyticsListener(EventLogger(null))

        setupClosedCaptions()

        videoApi = VideoApi(videoView)
        playlistManager.addVideoApi(videoApi)
        playlistManager.play(0, false)

        (videoView.videoControls as? DefaultVideoControls)?.visibilityListener =
            ControlsVisibilityListener()
    }

    private fun setupClosedCaptions() {
        captionsButton = AppCompatImageButton(this).apply {
            setBackgroundResource(android.R.color.transparent)
      setImageResource(R.drawable.ic_closed_caption_white_24dp)
            setOnClickListener { showCaptionsMenu() }
        }

        (videoView.videoControls as? DefaultVideoControls)?.let {
            it.seekListener = this
            if (videoView.trackSelectionAvailable()) {
                it.addExtraView(captionsButton)
            }
        }

        videoView.setOnVideoSizedChangedListener(object : OnVideoSizeChangedListener {
          override fun onVideoSizeChanged(
            intrinsicWidth: Int,
            intrinsicHeight: Int,
            pixelWidthHeightRatio: Float
          ) {
            val videoAspectRatio: Float = if (intrinsicWidth == 0 || intrinsicHeight == 0) {
              1f
            } else {
              intrinsicWidth * pixelWidthHeightRatio / intrinsicHeight
            }

            subtitleFrameLayout.setAspectRatio(videoAspectRatio)
          }
        })

        videoView.setCaptionListener(subtitleView)
    }

    private fun setupPlaylistManager() {
        playlistManager = (applicationContext as ExoPlayerApp).playlistManager

        val mediaItems = Samples.getVideoSamples().map {
            MediaItem(it, false)
        }

        playlistManager.setParameters(mediaItems, selectedIndex)
        playlistManager.id = PLAYLIST_ID.toLong()
    }

    private fun showCaptionsMenu() {
        val captionItems = captionPopupManager.getCaptionItems(videoView)
        if (captionItems.isEmpty()) {
            return
        }

        captionPopupManager.showCaptionsMenu(captionItems, captionsButton) {
            onTrackSelected(it)
        }
    }

    private fun onTrackSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true

        when (val itemId = menuItem.itemId) {
          CC_DEFAULT -> videoView.clearSelectedTracks(RendererType.CLOSED_CAPTION)
          CC_DISABLED -> videoView.setRendererEnabled(RendererType.CLOSED_CAPTION, false)
            else -> {
                val trackIndex = itemId % CC_GROUP_INDEX_MOD
                val groupIndex = itemId / CC_GROUP_INDEX_MOD
                videoView.setTrack(RendererType.CLOSED_CAPTION, groupIndex, trackIndex)
            }
        }

        return true
    }

    /**
     * A Listener for the [DefaultVideoControls]
     * so that we can re-enter fullscreen mode when the controls are hidden.
     */
    private inner class ControlsVisibilityListener : VideoControlsVisibilityListener {
        override fun onControlsShown() {
//            fullscreenManager.exitFullscreen()
        }

        override fun onControlsHidden() {
//            fullscreenManager.enterFullscreen()
        }
    }
}