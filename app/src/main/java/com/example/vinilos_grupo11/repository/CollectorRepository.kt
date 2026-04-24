package com.example.vinilos_grupo11.repository

import android.content.Context
import com.example.vinilos_grupo11.database.dao.CollectorDao
import com.example.vinilos_grupo11.model.Collector
import com.example.vinilos_grupo11.network.CollectorServiceAdapter

class CollectorRepository(context: Context) : ICollectorRepository {

    private val serviceAdapter = CollectorServiceAdapter(context)
    private val dao = CollectorDao

    override fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: () -> Unit
    ) {
        val cached = dao.getAll()
        if (cached != null) {
            onSuccess(cached)
            return
        }
        serviceAdapter.getCollectors(
            onSuccess = { collectors ->
                dao.insertAll(collectors)
                onSuccess(collectors)
            },
            onError = { onError() }
        )
    }
}
