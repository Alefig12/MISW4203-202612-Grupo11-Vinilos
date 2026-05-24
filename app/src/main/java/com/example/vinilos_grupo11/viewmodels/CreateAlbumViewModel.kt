package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vinilos_grupo11.models.AlbumCreateRequest
import com.example.vinilos_grupo11.performance.PerformanceTracker
import com.example.vinilos_grupo11.repositories.IAlbumRepository
import kotlinx.coroutines.launch

class CreateAlbumViewModel(private val repository: IAlbumRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _saveError = MutableLiveData<String?>(null)
    val saveError: LiveData<String?> = _saveError

    fun saveAlbum(request: AlbumCreateRequest) {
        _isLoading.value = true
        _saveError.value = null
        viewModelScope.launch {
            val t0 = PerformanceTracker.beginSection("HU07-CreateAlbum")
            var success = false
            try {
                repository.postAlbum(request)
                _saveSuccess.value = true
                success = true
            } catch (e: Exception) {
                _saveError.value = e.message
            } finally {
                PerformanceTracker.endSection("HU07-CreateAlbum", t0, success)
                _isLoading.value = false
            }
        }
    }

    companion object {
        var testRepositoryFactory: ((Application) -> IAlbumRepository)? = null

        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = application
                val repo = testRepositoryFactory?.invoke(app)
                    ?: (app as com.example.vinilos_grupo11.application.VinilosApplication).albumRepository
                CreateAlbumViewModel(repo)
            }
        }
    }
}
