package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Artist

interface IArtistRepository {
    fun getArtists(
        onSuccess: (List<Artist>) -> Unit,
        onError: () -> Unit
    )

    fun getArtistDetail(
        artistId: Int,
        onSuccess: (Artist) -> Unit,
        onError: () -> Unit
    )
}
