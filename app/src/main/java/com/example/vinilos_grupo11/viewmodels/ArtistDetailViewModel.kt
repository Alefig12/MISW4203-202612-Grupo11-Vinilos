package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vinilos_grupo11.models.Artist
import com.example.vinilos_grupo11.repositories.ArtistRepository
import com.example.vinilos_grupo11.repositories.IArtistRepository

class ArtistDetailViewModel(private val repository: IArtistRepository) : ViewModel() {

    private val _artist = MutableLiveData<Artist?>()
    val artist: LiveData<Artist?> = _artist

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = _hasError

    fun loadArtistDetail(artistId: Int) {
        _isLoading.value = true
        _hasError.value = false
        repository.getArtistDetail(
            artistId = artistId,
            onSuccess = { artistDetail ->
                _artist.postValue(artistDetail)
                _isLoading.postValue(false)
            },
            onError = {
                _hasError.postValue(true)
                _isLoading.postValue(false)
            }
        )
    }

    companion object {
        var testRepositoryFactory: ((Application) -> IArtistRepository)? = null

        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = testRepositoryFactory?.invoke(application)
                    ?: ArtistRepository(application)
                ArtistDetailViewModel(repo)
            }
        }
    }
}
