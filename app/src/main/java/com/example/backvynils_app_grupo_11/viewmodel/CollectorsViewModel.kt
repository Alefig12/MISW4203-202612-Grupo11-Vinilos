package com.example.backvynils_app_grupo_11.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.backvynils_app_grupo_11.model.Collector
import com.example.backvynils_app_grupo_11.repository.CollectorRepository
import com.example.backvynils_app_grupo_11.repository.ICollectorRepository

class CollectorsViewModel(private val repository: ICollectorRepository) : ViewModel() {

    private val _collectors = MutableLiveData<List<Collector>>(emptyList())
    val collectors: LiveData<List<Collector>> = _collectors

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = _hasError

    fun loadCollectors() {
        _isLoading.value = true
        _hasError.value = false
        repository.getCollectors(
            onSuccess = { list ->
                _collectors.postValue(list)
                _isLoading.postValue(false)
            },
            onError = {
                _hasError.postValue(true)
                _isLoading.postValue(false)
            }
        )
    }

    companion object {
        // Permite inyectar un repositorio falso en pruebas instrumentadas
        var testRepositoryFactory: ((Application) -> ICollectorRepository)? = null

        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = testRepositoryFactory?.invoke(application)
                    ?: CollectorRepository(application)
                CollectorsViewModel(repo)
            }
        }
    }
}
