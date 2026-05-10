package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import com.example.vinilos_grupo11.R
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.models.Collector
import org.json.JSONArray
import org.json.JSONObject

class CollectorServiceAdapter(private val context: Context) {

    private val networkAdapter: NetworkServiceAdapter by lazy {
        NetworkServiceAdapter.getInstance(context)
    }

    fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val url = "${baseUrl}/collectors"
        val request = JsonArrayRequest(
            url,
            { response -> onSuccess(parseCollectors(response)) },
            { error -> onError(error) }
        )
        networkAdapter.addToRequestQueue(request)
    }

    fun getCollectorDetail(
        collectorId: Int,
        onSuccess: (Collector) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baseUrl = context.getString(R.string.base_url)
        val url = "${baseUrl}/collectors/$collectorId"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val collector = parseCollector(response)
                    val favoriteAlbumIds = parseFavoriteAlbumIds(response)
                    if (favoriteAlbumIds.isEmpty()) {
                        onSuccess(collector)
                    } else {
                        getAlbumsByIds(
                            albumIds = favoriteAlbumIds,
                            onSuccess = { albums ->
                                onSuccess(collector.copy(favoriteAlbums = albums))
                            },
                            onError = onError
                        )
                    }
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error -> onError(error) }
        )
        networkAdapter.addToRequestQueue(request)
    }

    private fun parseCollectors(jsonArray: JSONArray): List<Collector> {
        val collectors = mutableListOf<Collector>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            collectors.add(parseCollector(item))
        }
        return collectors
    }

    private fun parseCollector(item: JSONObject): Collector {
        return Collector(
            id = item.getInt("id"),
            name = item.getString("name"),
            telephone = item.optString("telephone", ""),
            email = item.optString("email", ""),
            image = item.optString("image", "")
        )
    }

    private fun parseFavoriteAlbumIds(item: JSONObject): List<Int> {
        val albumIds = mutableListOf<Int>()
        val albumsArray = item.optJSONArray("collectorAlbums")
        if (albumsArray != null) {
            for (i in 0 until albumsArray.length()) {
                albumIds.add(albumsArray.getJSONObject(i).optInt("id", -1))
            }
        }
        return albumIds.filter { it != -1 }
    }

    private fun getAlbumsByIds(
        albumIds: List<Int>,
        onSuccess: (List<Album>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (albumIds.isEmpty()) { onSuccess(emptyList()); return }

        val baseUrl = context.getString(R.string.base_url)
        val results = arrayOfNulls<Album>(albumIds.size)
        val remaining = AtomicInteger(albumIds.size)
        val failed = AtomicBoolean(false)

        albumIds.forEachIndexed { index, albumId ->
            val request = JsonObjectRequest(
                Request.Method.GET,
                "${baseUrl}/albums/$albumId",
                null,
                { response ->
                    try {
                        results[index] = Album(
                            albumId = response.getInt("id"),
                            name = response.optString("name", ""),
                            cover = response.optString("cover", ""),
                            releaseDate = response.optString("releaseDate", ""),
                            description = response.optString("description", ""),
                            genre = response.optString("genre", ""),
                            recordLabel = response.optString("recordLabel", "")
                        )
                        if (remaining.decrementAndGet() == 0 && !failed.get()) {
                            onSuccess(results.filterNotNull())
                        }
                    } catch (e: Exception) {
                        if (!failed.getAndSet(true)) onError(e)
                    }
                },
                { error -> if (!failed.getAndSet(true)) onError(error) }
            )
            networkAdapter.addToRequestQueue(request)
        }
    }
}
