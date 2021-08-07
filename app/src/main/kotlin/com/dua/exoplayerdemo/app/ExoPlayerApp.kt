package com.dua.exoplayerdemo.app

import android.app.Application
import android.content.Context
import com.devbrackets.android.exomedia.nmp.config.PlayerConfig
import com.devbrackets.android.exomedia.nmp.config.PlayerConfigBuilder
import com.devbrackets.android.exomedia.nmp.config.PlayerConfigProvider

import com.dua.exoplayerdemo.manager.PlaylistManager
import com.dua.exoplayerdemo.util.OkHttpDataSourceFactoryProvider



class ExoPlayerApp : Application() {
    val playlistManager: PlaylistManager by lazy { PlaylistManager(this) }

    override fun onCreate() {
        super.onCreate()
    }


    @Suppress("unused")
    class OkPlayerConfigProvider: PlayerConfigProvider {
        override fun getConfig(context: Context): PlayerConfig {
            return PlayerConfigBuilder(context)
                .setDataSourceFactoryProvider(OkHttpDataSourceFactoryProvider(context))
                .build()
        }
    }
}
