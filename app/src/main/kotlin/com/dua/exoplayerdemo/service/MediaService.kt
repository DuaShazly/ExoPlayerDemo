package com.dua.exoplayerdemo.service


import com.dua.exoplayerdemo.data.MediaItem
import com.dua.exoplayerdemo.manager.PlaylistManager
import com.dua.exoplayerdemo.playlist.AudioApi
import com.devbrackets.android.playlistcore.components.playlisthandler.DefaultPlaylistHandler
import com.devbrackets.android.playlistcore.components.playlisthandler.PlaylistHandler
import com.devbrackets.android.playlistcore.service.BasePlaylistService
import com.dua.exoplayerdemo.app.ExoPlayerApp

/**
 * A simple service that extends [BasePlaylistService] in order to provide
 * the application specific information required.
 */
class MediaService : BasePlaylistService<MediaItem, PlaylistManager>() {

    override val playlistManager by lazy { (applicationContext as ExoPlayerApp).playlistManager }

    override fun onCreate() {
        super.onCreate()

        // Adds the audio player implementation, otherwise there's nothing to play media with
        playlistManager.mediaPlayers.add(AudioApi(applicationContext))
    }

    override fun onDestroy() {
        super.onDestroy()

        // Releases and clears all the MediaPlayersMediaImageProvider
        playlistManager.mediaPlayers.forEach {
            it.release()
        }

        playlistManager.mediaPlayers.clear()
    }

    override fun newPlaylistHandler(): PlaylistHandler<MediaItem> {
        val imageProvider = MediaImageProvider(applicationContext) {
            playlistHandler.updateMediaControls()
        }

        return DefaultPlaylistHandler.Builder(
                applicationContext,
                javaClass,
                playlistManager,
                imageProvider
        ).build()
    }
}