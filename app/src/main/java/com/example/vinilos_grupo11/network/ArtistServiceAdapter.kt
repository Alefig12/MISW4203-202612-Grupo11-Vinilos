package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.models.Artist
import org.json.JSONArray

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

    private fun parseArtists(jsonArray: JSONArray): List<Artist> {
        val artists = mutableListOf<Artist>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            artists.add(
                Artist(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    image = item.optString("image", ""),
                    description = item.optString("description", ""),
                    birthDate = item.optString("birthDate", "")
                )
            )
        }
        return artists
    }
}

