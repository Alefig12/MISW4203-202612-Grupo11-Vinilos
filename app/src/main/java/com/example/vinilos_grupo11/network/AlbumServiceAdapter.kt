package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.models.CommentSummary
import com.example.vinilos_grupo11.models.PerformerSummary
import com.example.vinilos_grupo11.models.Track
import org.json.JSONObject
import com.example.vinilos_grupo11.models.AlbumCreateRequest

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

    fun getAlbumById(
        albumId: Int,
        onSuccess: (AlbumDetail) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val request = JsonObjectRequest(
            Request.Method.GET,
            "${baseUrl}/albums/${albumId}",
            null,
            { response ->
                try {
                    onSuccess(parseAlbumDetail(response))
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error -> onError(error) }
        )
        broker.requestQueue.add(request)
    }

    fun createAlbum(
        request: AlbumCreateRequest,
        onSuccess: (Album) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val body = JSONObject().apply {
            put("name", request.name)
            put("cover", request.cover)
            put("releaseDate", request.releaseDate)
            put("description", request.description)
            put("genre", request.genre)
            put("recordLabel", request.recordLabel)
        }
        val req = JsonObjectRequest(
            Request.Method.POST,
            "${baseUrl}/albums",
            body,
            { response ->
                try {
                    onSuccess(
                        Album(
                            albumId = response.getInt("id"),
                            name = response.getString("name"),
                            cover = response.getString("cover"),
                            releaseDate = response.getString("releaseDate"),
                            description = response.getString("description"),
                            genre = response.getString("genre"),
                            recordLabel = response.getString("recordLabel")
                        )
                    )
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error -> onError(error) }
        )
        broker.requestQueue.add(req)
    }

    private fun parseAlbumDetail(response: JSONObject): AlbumDetail {
        val tracks = mutableListOf<Track>()
        val tracksArray = response.optJSONArray("tracks")
        if (tracksArray != null) {
            for (i in 0 until tracksArray.length()) {
                val item = tracksArray.getJSONObject(i)
                tracks.add(
                    Track(
                        id = item.optInt("id", 0),
                        name = item.optString("name", ""),
                        duration = item.optString("duration", "0:00")
                    )
                )
            }
        }

        val performers = mutableListOf<PerformerSummary>()
        val performersArray = response.optJSONArray("performers")
        if (performersArray != null) {
            for (i in 0 until performersArray.length()) {
                val item = performersArray.getJSONObject(i)
                performers.add(
                    PerformerSummary(
                        id = item.optInt("id", 0),
                        name = item.optString("name", ""),
                        image = item.optString("image", "")
                    )
                )
            }
        }

        val comments = mutableListOf<CommentSummary>()
        val commentsArray = response.optJSONArray("comments")
        if (commentsArray != null) {
            for (i in 0 until commentsArray.length()) {
                val item = commentsArray.getJSONObject(i)
                comments.add(
                    CommentSummary(
                        id = item.optInt("id", 0),
                        description = item.optString("description", ""),
                        rating = item.optInt("rating", 0)
                    )
                )
            }
        }

        return AlbumDetail(
            id = response.getInt("id"),
            name = response.optString("name", ""),
            cover = response.optString("cover", ""),
            releaseDate = response.optString("releaseDate", ""),
            description = response.optString("description", ""),
            genre = response.optString("genre", ""),
            recordLabel = response.optString("recordLabel", ""),
            tracks = tracks,
            performers = performers,
            comments = comments
        )
    }

    fun addTrackToAlbum(
        albumId: Int,
        track: Track,
        onSuccess: (Track) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val body = JSONObject().apply {
            put("name", track.name)
            put("duration", track.duration)
        }
        val request = JsonObjectRequest(
            Request.Method.POST,
            "${baseUrl}/albums/$albumId/tracks",
            body,
            { response ->
                onSuccess(
                    Track(
                        id = response.optInt("id", 0),
                        name = response.optString("name", ""),
                        duration = response.optString("duration", "")
                    )
                )
            },
            { error -> onError(error) }
        )
        broker.requestQueue.add(request)
    }

}