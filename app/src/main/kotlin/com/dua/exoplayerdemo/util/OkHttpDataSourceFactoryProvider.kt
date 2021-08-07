package com.dua.exoplayerdemo.util

import android.content.Context
import com.devbrackets.android.exomedia.core.source.data.DataSourceFactoryProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import okhttp3.OkHttpClient
import java.io.File

class OkHttpDataSourceFactoryProvider(context: Context): DataSourceFactoryProvider {
  private val cacheDir = context.cacheDir
  private var instance: CacheDataSource.Factory? = null

  override fun provide(userAgent: String, listener: TransferListener?): DataSource.Factory {
    instance?.let {
      return it
    }

    val upstreamFactory = OkHttpDataSource.Factory(OkHttpClient()).apply {
      setUserAgent(userAgent)
      setTransferListener(listener)
    }

    val cache = SimpleCache(File(cacheDir, "ExoMediaCache"), LeastRecentlyUsedCacheEvictor((50 * 1024 * 1024).toLong()))

    instance = CacheDataSource.Factory().apply {
      setCache(cache)
      setUpstreamDataSourceFactory(upstreamFactory)
      setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    return instance!!
  }

}