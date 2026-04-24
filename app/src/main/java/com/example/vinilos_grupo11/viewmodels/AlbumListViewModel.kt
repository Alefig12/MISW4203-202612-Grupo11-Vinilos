package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vinilos_grupo11.models.Album
import com.example.vinilos_grupo11.repositories.AlbumRepository

class AlbumListViewModel(application: Application, private val repository: AlbumRepository) :
    AndroidViewModel(application) {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean> get() = _eventNetworkError

    private val _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean> get() = _isNetworkErrorShown

    init {
        refreshDataFromNetwork()
    }

    private fun refreshDataFromNetwork() {
        repository.refreshData(
            onSuccess = { albums ->
                _albums.postValue(albums)
                _eventNetworkError.value = false
                _isNetworkErrorShown.value = false
            },
            onError = {
                _eventNetworkError.value = true
            }
        )

    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    class Factory(
        private val app: Application,
        private val repository: AlbumRepository
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