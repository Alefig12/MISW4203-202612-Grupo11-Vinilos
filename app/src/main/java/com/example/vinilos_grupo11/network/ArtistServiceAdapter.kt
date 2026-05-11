package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.Artist
import org.json.JSONArray
import org.json.JSONObject

class ArtistServiceAdapter(private val context: Context) {

    private val networkAdapter: NetworkServiceAdapter by lazy {
        NetworkServiceAdapter.getInstance(context)
    }

    fun getArtists(
        onSuccess: (List<Artist>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val url = "${baseUrl}/musicians"
        val request = JsonArrayRequest(
            url,
            { response -> onSuccess(parseArtists(response)) },
            { error -> onError(error) }
        )
        networkAdapter.addToRequestQueue(request)
    }

    fun getArtistDetail(
        artistId: Int,
        onSuccess: (Artist) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val url = "${baseUrl}/musicians/$artistId"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response -> onSuccess(parseArtist(response)) },
            { error -> onError(error) }
        )
        networkAdapter.addToRequestQueue(request)
    }

    private fun parseArtists(jsonArray: JSONArray): List<Artist> {
        val artists = mutableListOf<Artist>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            artists.add(parseArtist(item))
        }
        return artists
    }

    private fun parseArtist(item: JSONObject): Artist {
        val albums = mutableListOf<Album>()
        if (item.has("albums")) {
            val albumsArray = item.getJSONArray("albums")
            for (i in 0 until albumsArray.length()) {
                val albumItem = albumsArray.getJSONObject(i)
                albums.add(
                    Album(
                        albumId = albumItem.getInt("id"),
                        name = albumItem.getString("name"),
                        cover = albumItem.getString("cover"),
                        releaseDate = albumItem.getString("releaseDate"),
                        description = albumItem.getString("description"),
                        genre = albumItem.getString("genre"),
                        recordLabel = albumItem.getString("recordLabel")
                    )
                )
            }
        }

        return Artist(
            id = item.getInt("id"),
            name = item.getString("name"),
            image = item.optString("image", ""),
            description = item.optString("description", ""),
            birthDate = item.optString("birthDate", ""),
            albums = albums
        )
    }
}
