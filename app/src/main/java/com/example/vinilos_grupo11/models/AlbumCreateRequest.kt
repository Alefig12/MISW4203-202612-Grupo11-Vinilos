package com.example.vinilos_grupo11.models

data class AlbumCreateRequest(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String
)
