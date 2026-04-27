package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class NetworkServiceAdapter private constructor(context: Context) {

    companion object {
        // 10.0.2.2 es la IP especial del emulador Android para acceder al localhost del PC
        const val BASE_URL = "http://10.0.2.2:3000/"

        @Volatile
        private var INSTANCE: NetworkServiceAdapter? = null

        fun getInstance(context: Context): NetworkServiceAdapter =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: NetworkServiceAdapter(context).also { INSTANCE = it }
            }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }
}
