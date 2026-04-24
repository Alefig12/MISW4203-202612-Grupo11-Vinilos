package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Album

interface IAlbumRepository {
    fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit)
}
