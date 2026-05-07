package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vinilos_grupo11.models.AlbumDetail
import com.example.vinilos_grupo11.repositories.IAlbumRepository

class AlbumDetailViewModel(
    application: Application,
    private val repository: IAlbumRepository,
    private val albumId: Int
) : AndroidViewModel(application) {

    private val _albumDetail = MutableLiveData<AlbumDetail>()
    val albumDetail: LiveData<AlbumDetail> get() = _albumDetail

    private val _eventNetworkError = MutableLiveData<Boolean>(false)
    val eventNetworkError: LiveData<Boolean> get() = _eventNetworkError

    private val _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean> get() = _isNetworkErrorShown

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        loadAlbumDetail()
    }

    private fun loadAlbumDetail() {
        _isLoading.value = true
        repository.getAlbumDetail(
            albumId,
            onSuccess = { detail ->
                _albumDetail.postValue(detail)
                _eventNetworkError.postValue(false)
                _isNetworkErrorShown.postValue(false)
                _isLoading.postValue(false)
            },
            onError = {
                _eventNetworkError.postValue(true)
                _isLoading.postValue(false)
            }
        )
    }

    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    companion object {
        var testRepositoryFactory: ((Application) -> IAlbumRepository)? = null
    }

    class Factory(
        private val app: Application,
        private val repository: IAlbumRepository,
        private val albumId: Int
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlbumDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlbumDetailViewModel(app, repository, albumId) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
