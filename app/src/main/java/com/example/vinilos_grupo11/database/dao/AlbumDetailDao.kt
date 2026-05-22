package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.AlbumDetail

object AlbumDetailDao {
    private val cache = mutableMapOf<Int, AlbumDetail>()

    fun get(albumId: Int): AlbumDetail? = cache[albumId]

    fun put(albumId: Int, detail: AlbumDetail) {
        cache[albumId] = detail
    }

    fun remove(albumId: Int) {
        cache.remove(albumId)
    }

    fun clearCache() {
        cache.clear()
    }
}
