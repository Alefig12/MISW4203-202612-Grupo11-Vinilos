package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vinilos_grupo11.models.Artist
import com.example.vinilos_grupo11.performance.PerformanceTracker
import com.example.vinilos_grupo11.repositories.ArtistRepository
import com.example.vinilos_grupo11.repositories.IArtistRepository
import kotlinx.coroutines.launch

class ArtistViewModel(private val repository: IArtistRepository) : ViewModel() {

    private val _artists = MutableLiveData<List<Artist>>(emptyList())
    val artists: LiveData<List<Artist>> = _artists

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = _hasError

    fun loadArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val t0 = PerformanceTracker.beginSection("HU03-ArtistCatalog")
            val result = repository.fetchArtists()
            val success = result != null
            PerformanceTracker.endSection("HU03-ArtistCatalog", t0, success)
            if (success) {
                _artists.value = result!!
            } else {
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    companion object {
        var testRepositoryFactory: ((Application) -> IArtistRepository)? = null

        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = testRepositoryFactory?.invoke(application)
                    ?: ArtistRepository(application)
                ArtistViewModel(repo)
            }
        }
    }
}
