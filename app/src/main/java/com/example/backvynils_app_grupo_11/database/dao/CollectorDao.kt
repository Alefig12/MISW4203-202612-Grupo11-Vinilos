package com.example.backvynils_app_grupo_11.database.dao

import com.example.backvynils_app_grupo_11.model.Collector

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
