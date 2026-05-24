package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.database.dao.AlbumDao
import com.example.vinilos_grupo11.database.dao.AlbumDetailDao
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumCreateRequest
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.network.AlbumServiceAdapter
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AlbumRepository(private val adapter: AlbumServiceAdapter) : IAlbumRepository {

    private val dao = AlbumDao
    private val detailDao = AlbumDetailDao

    override fun getCachedAlbums(): List<Album>? = dao.getAll()

    override fun refreshData(
        onSuccess: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val cached = dao.getAll()
        if (cached != null) {
            onSuccess(cached)
            return
        }
        adapter.getAlbums(
            onSuccess = { albums ->
                dao.insertAll(albums)
                onSuccess(albums)
            },
            onError = onError
        )
    }

    // Always fetches from network and updates cache — used for stale-while-revalidate
    override suspend fun fetchFreshAlbums(): List<Album> = suspendCancellableCoroutine { continuation ->
        adapter.getAlbums(
            onSuccess = { albums ->
                dao.insertAll(albums)
                continuation.resume(albums)
            },
            onError = { continuation.resumeWithException(it) }
        )
    }

    override fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val cached = detailDao.get(albumId)
        if (cached != null) {
            onSuccess(cached)
            return
        }
        adapter.getAlbumById(
            albumId,
            onSuccess = { detail ->
                detailDao.put(albumId, detail)
                onSuccess(detail)
            },
            onError = onError
        )
    }

    override fun addTrackToAlbum(
        albumId: Int,
        track: com.example.vinilos_grupo11.models.Track,
        onSuccess: (com.example.vinilos_grupo11.models.Track) -> Unit,
        onError: (Exception) -> Unit
    ) {
        adapter.addTrackToAlbum(
            albumId,
            track,
            onSuccess = { newTrack ->
                detailDao.remove(albumId)
                onSuccess(newTrack)
            },
            onError = onError
        )
    }

    override fun createAlbum(
        request: AlbumCreateRequest,
        onSuccess: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        adapter.createAlbum(
            request,
            onSuccess = { album ->
                dao.clearCache()
                onSuccess(album)
            },
            onError = onError
        )
    }
}
