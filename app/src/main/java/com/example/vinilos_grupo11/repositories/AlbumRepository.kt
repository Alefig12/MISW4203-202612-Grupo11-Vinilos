package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.network.AlbumServiceAdapter

class AlbumRepository(private val adapter: AlbumServiceAdapter) : IAlbumRepository {

    override fun refreshData(
        onSuccess: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        adapter.getAlbums(onSuccess, onError)
    }

}