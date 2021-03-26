package com.curiouswizard.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.curiouswizard.devbyteviewer.database.VideosDatabase
import com.curiouswizard.devbyteviewer.database.asDomainModel
import com.curiouswizard.devbyteviewer.domain.Video
import com.curiouswizard.devbyteviewer.network.Network
import com.curiouswizard.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */

class VideosRepository(private val database: VideosDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val videos: LiveData<List<Video>> =
            Transformations.map(database.videoDao.getVideos()) {
                it.asDomainModel()
            }

    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            val playlist = Network.devbytes.getPlaylist()
            database.videoDao.insertAll(*playlist.asDatabaseModel())
        }
    }
}