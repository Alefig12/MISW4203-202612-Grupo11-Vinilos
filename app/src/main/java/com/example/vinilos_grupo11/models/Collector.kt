package com.example.vinilos_grupo11.models

data class Collector(
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val image: String = "",
    val favoriteAlbums: List<Album> = emptyList()
)
