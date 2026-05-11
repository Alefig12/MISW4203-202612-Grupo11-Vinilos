package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.performance.PerformanceTracker
import com.example.vinilos_grupo11.repositories.IAlbumRepository
import kotlinx.coroutines.launch

class AlbumListViewModel(application: Application, private val repository: IAlbumRepository) :
    AndroidViewModel(application) {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean> get() = _eventNetworkError

    private val _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean> get() = _isNetworkErrorShown

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        refreshDataFromNetwork()
    }

    private fun refreshDataFromNetwork() {
        viewModelScope.launch {
            val t0 = PerformanceTracker.beginSection("HU01-AlbumCatalog")
            var success = false

            // Show cached data immediately — user sees content with no spinner
            val cached = repository.getCachedAlbums()
            if (cached != null) {
                _albums.value = cached
            } else {
                _isLoading.value = true
            }

            try {
                val fresh = repository.fetchFreshAlbums()
                _albums.value = fresh
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
                success = true
            } catch (e: Exception) {
                // Only show error if user has no data to look at
                if (cached == null) {
                    _eventNetworkError.value = true
                }
            } finally {
                PerformanceTracker.endSection("HU01-AlbumCatalog", t0, success)
                _isLoading.value = false
            }
        }
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    companion object {
        var testRepositoryFactory: ((Application) -> IAlbumRepository)? = null
    }

    class Factory(
        private val app: Application,
        private val repository: IAlbumRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlbumListViewModel(app, repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
