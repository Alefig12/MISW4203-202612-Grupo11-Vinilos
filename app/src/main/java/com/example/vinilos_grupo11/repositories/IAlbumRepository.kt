package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumDetail

interface IAlbumRepository {
    fun refreshData(onSuccess: (List<Album>) -> Unit, onError: (Exception) -> Unit)
    fun getAlbumDetail(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    )
}
