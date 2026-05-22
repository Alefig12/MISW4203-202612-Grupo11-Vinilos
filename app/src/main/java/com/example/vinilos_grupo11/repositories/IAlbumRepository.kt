package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumDetail
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface IAlbumRepository {

    fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit)

    fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        onError(UnsupportedOperationException("getAlbumDetail not implemented"))
    }

    fun getCachedAlbums(): List<Album>? = null

    suspend fun fetchAlbums(): List<Album> = suspendCancellableCoroutine { continuation ->
        refreshData(
            onSuccess = { continuation.resume(it) },
            onError = { continuation.resumeWithException(it) }
        )
    }

    suspend fun fetchFreshAlbums(): List<Album> = fetchAlbums()

    suspend fun fetchAlbumDetail(albumId: Int): AlbumDetail =
        suspendCancellableCoroutine { continuation ->
            getAlbumDetail(
                albumId,
                onSuccess = { continuation.resume(it) },
                onError = { continuation.resumeWithException(it) }
            )
        }

    fun addTrackToAlbum(
        albumId: Int,
        track: com.example.vinilos_grupo11.models.Track,
        onSuccess: (com.example.vinilos_grupo11.models.Track) -> Unit,
        onError: (Exception) -> Unit
    ) {
        onError(UnsupportedOperationException("addTrackToAlbum not implemented"))
    }
}
