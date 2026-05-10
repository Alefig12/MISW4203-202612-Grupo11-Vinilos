package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.Artist

object ArtistDetailDao {
    private val cache = mutableMapOf<Int, Artist>()

    fun get(artistId: Int): Artist? = cache[artistId]

    fun put(artistId: Int, artist: Artist) {
        cache[artistId] = artist
    }

    fun clearCache() {
        cache.clear()
    }
}
