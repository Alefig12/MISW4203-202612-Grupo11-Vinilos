package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Artist
import com.example.vinilos_grupo11.repositories.IArtistRepository

class FakeArtistRepository(private val shouldFail: Boolean = false) : IArtistRepository {

    val fakeArtists = listOf(
        Artist(id = 1, name = "Queen", image = "https://example.com/queen.jpg", description = "Rock band", birthDate = "1970-01-01T00:00:00.000Z"),
        Artist(id = 2, name = "The Beatles", image = "https://example.com/beatles.jpg", description = "Legendary band", birthDate = "1960-01-01T00:00:00.000Z"),
        Artist(id = 3, name = "David Bowie", image = "https://example.com/bowie.jpg", description = "Starman", birthDate = "1947-01-08T00:00:00.000Z")
    )

    override fun getArtists(
        onSuccess: (List<Artist>) -> Unit,
        onError: () -> Unit
    ) {
        if (shouldFail) {
            onError()
        } else {
            onSuccess(fakeArtists)
        }
    }

    override fun getArtistDetail(
        artistId: Int,
        onSuccess: (Artist) -> Unit,
        onError: () -> Unit
    ) {
        if (shouldFail) {
            onError()
        } else {
            onSuccess(fakeArtists.firstOrNull { it.id == artistId } ?: fakeArtists.first())
        }
    }
}
