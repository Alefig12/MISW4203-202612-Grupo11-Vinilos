package com.example.vinilos_grupo11.fake

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.repositories.IAlbumRepository

class FakeAlbumRepository : IAlbumRepository {
    override fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit) {
        onSuccess(emptyList())
    }
}
