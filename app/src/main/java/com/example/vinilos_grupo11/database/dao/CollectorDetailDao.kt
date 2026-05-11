package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.Collector

object CollectorDetailDao {
    private val cache = mutableMapOf<Int, Collector>()

    fun get(collectorId: Int): Collector? = cache[collectorId]

    fun put(collectorId: Int, collector: Collector) {
        cache[collectorId] = collector
    }

    fun clearCache() {
        cache.clear()
    }
}
