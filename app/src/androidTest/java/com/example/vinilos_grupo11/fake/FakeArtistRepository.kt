package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Artist
import com.example.vinilos_grupo11.repositories.IArtistRepository

class FakeArtistRepository(private val shouldFail: Boolean = false) : IArtistRepository {

    val fakeArtists = listOf(
        Artist(id = 1, name = "Queen", image = "https://example.com/queen.jpg", description = "Rock band", birthDate = "1970-01-01"),
        Artist(id = 2, name = "The Beatles", image = "https://example.com/beatles.jpg", description = "Legendary band", birthDate = "1960-01-01"),
        Artist(id = 3, name = "David Bowie", image = "https://example.com/bowie.jpg", description = "Starman", birthDate = "1947-01-08")
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
}