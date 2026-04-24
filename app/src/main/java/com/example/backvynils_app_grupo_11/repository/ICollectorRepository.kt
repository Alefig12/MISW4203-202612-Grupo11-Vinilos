package com.example.backvynils_app_grupo_11.repository

import com.example.backvynils_app_grupo_11.model.Collector

interface ICollectorRepository {
    fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: () -> Unit
    )
}
