package com.example.vinilos_grupo11.repository

import com.example.vinilos_grupo11.model.Collector

interface ICollectorRepository {
    fun getCollectors(
        onSuccess: (List<Collector>) -> Unit,
        onError: () -> Unit
    )
}
