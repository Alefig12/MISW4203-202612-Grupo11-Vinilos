package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.Album

object AlbumDao {
    private var cache: List<Album>? = null

    fun getAll(): List<Album>? = cache

    fun insertAll(albums: List<Album>) {
        cache = albums
    }

    fun clearCache() {
        cache = null
    }
}
