package com.example.vinilos_grupo11.models

data class AlbumDetail(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
    val tracks: List<Track>,
    val performers: List<PerformerSummary>,
    val comments: List<CommentSummary>
)

data class Track(
    val id: Int,
    val name: String,
    val duration: String
)

data class PerformerSummary(
    val id: Int,
    val name: String,
    val image: String
)

data class CommentSummary(
    val id: Int,
    val description: String,
    val rating: Int
)
