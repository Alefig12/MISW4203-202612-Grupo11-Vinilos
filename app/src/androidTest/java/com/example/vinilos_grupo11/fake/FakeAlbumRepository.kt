package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumCreateRequest
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.models.CommentSummary
import com.example.vinilos_grupo11.models.PerformerSummary
import com.example.vinilos_grupo11.models.Track
import com.example.vinilos_grupo11.repositories.IAlbumRepository

class FakeAlbumRepository(
    private val shouldFail: Boolean = false,
    private val hasCachedAlbums: Boolean = false
) : IAlbumRepository {

    val fakeAlbums = listOf(
        Album(albumId = 1, name = "A Night at the Opera", cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png", releaseDate = "1975-11-21", description = "Album by Queen", genre = "Rock", recordLabel = "EMI"),
        Album(albumId = 2, name = "Thriller", cover = "https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png", releaseDate = "1982-11-30", description = "Album by Michael Jackson", genre = "Pop", recordLabel = "Epic")
    )

    val fakeAlbumDetail = AlbumDetail(
        id = 1,
        name = "A Night at the Opera",
        cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
        releaseDate = "1975-11-21",
        description = "Album by Queen",
        genre = "Rock",
        recordLabel = "EMI",
        tracks = listOf(
            Track(id = 1, name = "Bohemian Rhapsody", duration = "5:55"),
            Track(id = 2, name = "You're My Best Friend", duration = "2:52")
        ),
        performers = listOf(PerformerSummary(id = 1, name = "Queen", image = "")),
        comments = listOf(CommentSummary(id = 1, description = "Great album", rating = 5))
    )

    override fun getCachedAlbums(): List<Album>? = if (hasCachedAlbums) fakeAlbums else null

    override fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit) {
        if (shouldFail) {
            onError(Exception("Network Error"))
        } else {
            onSuccess(fakeAlbums)
        }
    }

    override suspend fun fetchFreshAlbums(): List<Album> {
        if (shouldFail) throw Exception("Network Error")
        return fakeAlbums
    }

    override fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shouldFail) {
            onError(Exception("Network Error"))
        } else {
            onSuccess(fakeAlbumDetail)
        }
    }

    override fun createAlbum(
        request: AlbumCreateRequest,
        onSuccess: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (shouldFail) {
            onError(Exception("Network Error"))
        } else {
            onSuccess(
                Album(
                    albumId = 99,
                    name = request.name,
                    cover = request.cover,
                    releaseDate = request.releaseDate,
                    description = request.description,
                    genre = request.genre,
                    recordLabel = request.recordLabel
                )
            )
        }
    }
}
