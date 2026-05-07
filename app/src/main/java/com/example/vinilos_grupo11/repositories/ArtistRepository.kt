package com.example.vinilos_grupo11.repositories

import android.content.Context
import com.example.vinilos_grupo11.database.dao.ArtistDao
import com.example.vinilos_grupo11.models.Artist
import com.example.vinilos_grupo11.network.ArtistServiceAdapter

class ArtistRepository(context: Context) : IArtistRepository {

    private val serviceAdapter = ArtistServiceAdapter(context)
    private val dao = ArtistDao

    override fun getArtists(
        onSuccess: (List<Artist>) -> Unit,
        onError: () -> Unit
    ) {
        val cached = dao.getAll()
        if (cached != null) {
            onSuccess(cached)
            return
        }
        serviceAdapter.getArtists(
            onSuccess = { artists ->
                dao.insertAll(artists)
                onSuccess(artists)
            },
            onError = { onError() }
        )
    }

    override fun getArtistDetail(
        artistId: Int,
        onSuccess: (Artist) -> Unit,
        onError: () -> Unit
    ) {
        serviceAdapter.getArtistDetail(
            artistId = artistId,
            onSuccess = { artist ->
                onSuccess(artist)
            },
            onError = { onError() }
        )
    }
}

