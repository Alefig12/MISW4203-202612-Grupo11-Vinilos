package com.example.vinilos_grupo11.application

import android.app.Application
import com.example.vinilos_grupo11.network.AlbumServiceAdapter
import com.example.vinilos_grupo11.network.VolleyBroker
import com.example.vinilos_grupo11.repositories.AlbumRepository

class VinilosApplication : Application() {
    lateinit var volleyBroker: VolleyBroker
    lateinit var albumRepository: AlbumRepository
    override fun onCreate() {
        super.onCreate()

        volleyBroker = VolleyBroker(this)

        var albumAdapter = AlbumServiceAdapter(this,volleyBroker)
        albumRepository = AlbumRepository(albumAdapter)
    }
}