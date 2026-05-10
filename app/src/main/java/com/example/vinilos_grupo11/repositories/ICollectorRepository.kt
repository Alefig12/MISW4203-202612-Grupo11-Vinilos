package com.example.vinilos_grupo11.repositories

import com.example.vinilos_grupo11.models.Collector
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface ICollectorRepository {
    fun getCollectors(onSuccess: (List<Collector>) -> Unit, onError: () -> Unit)

    fun getCollectorDetail(
        collectorId: Int,
        onSuccess: (Collector) -> Unit,
        onError: () -> Unit
    ) {
        onError()
    }

    suspend fun fetchCollectors(): List<Collector>? = suspendCancellableCoroutine { continuation ->
        getCollectors(
            onSuccess = { continuation.resume(it) },
            onError = { continuation.resume(null) }
        )
    }

    suspend fun fetchCollectorDetail(collectorId: Int): Collector? = suspendCancellableCoroutine { continuation ->
        getCollectorDetail(
            collectorId = collectorId,
            onSuccess = { continuation.resume(it) },
            onError = { continuation.resume(null) }
        )
    }
}
