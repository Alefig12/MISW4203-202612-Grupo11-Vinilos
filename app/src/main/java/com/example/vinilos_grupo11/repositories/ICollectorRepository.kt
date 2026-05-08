package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Collector

interface ICollectorRepository {
    fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: () -> Unit
    )

    fun getCollectorDetail(
        collectorId: Int,
        onSuccess: (Collector) -> Unit,
        onError: () -> Unit
    )
}
