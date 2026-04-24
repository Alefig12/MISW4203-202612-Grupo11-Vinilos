package com.example.vinilos_grupo11.network

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyBroker(context: Context) {
    val requestQueue: RequestQueue = Volley.newRequestQueue(context.applicationContext)
}