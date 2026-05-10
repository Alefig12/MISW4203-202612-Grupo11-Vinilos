package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Artist
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface IArtistRepository {

    fun getArtists(onSuccess: (List<Artist>) -> Unit, onError: () -> Unit)

    fun getArtistDetail(
        artistId: Int,
        onSuccess: (Artist) -> Unit,
        onError: () -> Unit
    ) {
        onError()
    }

    suspend fun fetchArtists(): List<Artist>? = suspendCancellableCoroutine { continuation ->
        getArtists(
            onSuccess = { continuation.resume(it) },
            onError = { continuation.resume(null) }
        )
    }

    suspend fun fetchArtistDetail(artistId: Int): Artist? =
        suspendCancellableCoroutine { continuation ->
            getArtistDetail(
                artistId,
                onSuccess = { continuation.resume(it) },
                onError = { continuation.resume(null) }
            )
        }
}
