package com.example.vinilos_grupo11.database.dao

import com.example.vinilos_grupo11.models.Collector

object CollectorDao {
    private var cache: List<Collector>? = null

    fun getAll(): List<Collector>? = cache

    fun insertAll(collectors: List<Collector>) {
        cache = collectors
    }

    fun clearCache() {
        cache = null
    }
}
