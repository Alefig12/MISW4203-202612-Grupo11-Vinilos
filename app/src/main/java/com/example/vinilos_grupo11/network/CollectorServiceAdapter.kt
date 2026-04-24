package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.toolbox.JsonArrayRequest
import com.example.vinilos_grupo11.models.Collector
import org.json.JSONArray

class CollectorServiceAdapter(private val context: Context) {

    private val networkAdapter: NetworkServiceAdapter by lazy {
        NetworkServiceAdapter.getInstance(context)
    }

    fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val url = "${NetworkServiceAdapter.BASE_URL}collectors"
        val request = JsonArrayRequest(
            url,
            { response -> onSuccess(parseCollectors(response)) },
            { error -> onError(error) }
        )
        networkAdapter.addToRequestQueue(request)
    }

    private fun parseCollectors(jsonArray: JSONArray): List<Collector> {
        val collectors = mutableListOf<Collector>()
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            collectors.add(
                Collector(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    telephone = item.optString("telephone", ""),
                    email = item.optString("email", "")
                )
            )
        }
        return collectors
    }
}
