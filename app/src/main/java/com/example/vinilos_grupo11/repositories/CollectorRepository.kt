package com.example.vinilos_grupo11.repositories

import android.content.Context
import com.example.vinilos_grupo11.database.dao.CollectorDao
import com.example.vinilos_grupo11.database.dao.CollectorDetailDao
import com.example.vinilos_grupo11.models.Collector
import com.example.vinilos_grupo11.network.CollectorServiceAdapter

class CollectorRepository(context: Context) : ICollectorRepository {

    private val serviceAdapter = CollectorServiceAdapter(context)
    private val dao = CollectorDao
    private val detailDao = CollectorDetailDao

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

    override fun getCollectorDetail(
        collectorId: Int,
        onSuccess: (Collector) -> Unit,
        onError: () -> Unit
    ) {
        val cached = detailDao.get(collectorId)
        if (cached != null) {
            onSuccess(cached)
            return
        }
        serviceAdapter.getCollectorDetail(
            collectorId = collectorId,
            onSuccess = { collector ->
                detailDao.put(collectorId, collector)
                onSuccess(collector)
            },
            onError = { onError() }
        )
    }
}
