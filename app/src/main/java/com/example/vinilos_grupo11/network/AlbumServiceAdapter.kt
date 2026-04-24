package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.models.Album

//Aquí irán todos los metodos para álbumes (get, post, put etc)

class AlbumServiceAdapter(private val context: Context, private val broker: VolleyBroker) {

    fun getAlbums(
        onSuccess: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val request = JsonArrayRequest(
            "${baseUrl}/albums",
            { response ->
                val albums = mutableListOf<Album>()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    albums.add(
                        Album(
                            albumId = item.getInt("id"),
                            name = item.getString("name"),
                            cover = item.getString("cover"),
                            releaseDate = item.getString("releaseDate"),
                            description = item.getString("description"),
                            genre = item.getString("genre"),
                            recordLabel = item.getString("recordLabel")
                        )
                    )

                }
                onSuccess(albums)


            },
            { error -> onError(error)}
        )
        broker.requestQueue.add(request)
    }

}