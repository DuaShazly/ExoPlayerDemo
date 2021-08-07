package com.dua.exoplayerdemo.manager

import android.app.Application
import com.devbrackets.android.exomedia.ui.listener.VideoControlsButtonListener
import com.devbrackets.android.exomedia.ui.widget.controls.DefaultVideoControls
import com.devbrackets.android.playlistcore.manager.ListPlaylistManager

import com.dua.exoplayerdemo.data.MediaItem
import com.dua.exoplayerdemo.playlist.VideoApi
import com.dua.exoplayerdemo.service.MediaService


class PlaylistManager(application: Application) : ListPlaylistManager<MediaItem>(application, MediaService::class.java) {

    fun addVideoApi(videoApi: VideoApi) {
        mediaPlayers.add(videoApi)
        updateVideoControls(videoApi)
        registerPlaylistListener(videoApi)
    }


    fun removeVideoApi(videoApi: VideoApi) {
        (videoApi.videoView.videoControls as? DefaultVideoControls)?.setButtonListener(null)

        unRegisterPlaylistListener(videoApi)
        mediaPlayers.remove(videoApi)
    }


    private fun updateVideoControls(videoApi: VideoApi) {
        (videoApi.videoView.videoControls as? DefaultVideoControls)?.let {
            it.setPreviousButtonRemoved(false)
            it.setNextButtonRemoved(false)
            it.setButtonListener(ControlsListener())
        }
    }

    private inner class ControlsListener : VideoControlsButtonListener {
        override fun onPlayPauseClicked(): Boolean {
            invokePausePlay()
            return true
        }

        override fun onPreviousClicked(): Boolean {
            invokePrevious()
            return false
        }

        override fun onNextClicked(): Boolean {
            invokeNext()
            return false
        }

        override fun onRewindClicked(): Boolean {
            return false
        }

        override fun onFastForwardClicked(): Boolean {
            return false
        }
    }
}