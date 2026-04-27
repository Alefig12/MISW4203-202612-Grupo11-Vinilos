package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.Artist

object ArtistDao {
    private var cache: List<Artist>? = null

    fun getAll(): List<Artist>? = cache

    fun insertAll(artists: List<Artist>) {
        cache = artists
    }

    fun clearCache() {
        cache = null
    }
}

