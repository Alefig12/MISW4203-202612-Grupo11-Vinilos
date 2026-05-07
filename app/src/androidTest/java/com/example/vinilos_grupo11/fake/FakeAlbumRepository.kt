package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.repositories.IAlbumRepository

class FakeAlbumRepository(private val shouldFail: Boolean = false) : IAlbumRepository {

    val fakeAlbums = listOf(
        Album(albumId = 1, name = "A Night at the Opera", cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png", releaseDate = "1975-11-21", description = "Album by Queen", genre = "Rock", recordLabel = "EMI"),
        Album(albumId = 2, name = "Thriller", cover = "https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png", releaseDate = "1982-11-30", description = "Album by Michael Jackson", genre = "Pop", recordLabel = "Epic")
    )

    override fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit) {
        if (shouldFail) {
            onError(Exception("Network Error"))
        } else {
            onSuccess(fakeAlbums)
        }
    }

    override fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shouldFail) {
            onError(Exception("Network Error"))
        } else {
            val album = fakeAlbums.firstOrNull { it.albumId == albumId } ?: fakeAlbums.first()
            onSuccess(
                AlbumDetail(
                    id = album.albumId,
                    name = album.name,
                    cover = album.cover,
                    releaseDate = album.releaseDate,
                    description = album.description,
                    genre = album.genre,
                    recordLabel = album.recordLabel,
                    tracks = emptyList(),
                    performers = emptyList(),
                    comments = emptyList()
                )
            )
        }
    }
}
