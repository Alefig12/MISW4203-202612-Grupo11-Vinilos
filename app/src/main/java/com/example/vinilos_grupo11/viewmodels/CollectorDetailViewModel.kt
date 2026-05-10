package com.example.vinilos_grupo11.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vinilos_grupo11.models.Collector
import com.example.vinilos_grupo11.performance.PerformanceTracker
import com.example.vinilos_grupo11.repositories.CollectorRepository
import com.example.vinilos_grupo11.repositories.ICollectorRepository
import kotlinx.coroutines.launch

class CollectorDetailViewModel(private val repository: ICollectorRepository) : ViewModel() {

    private val _collector = MutableLiveData<Collector?>()
    val collector: LiveData<Collector?> = _collector

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> = _hasError

    fun loadCollectorDetail(collectorId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val t0 = PerformanceTracker.beginSection("HU06-CollectorDetail(id=$collectorId)")
            val result = repository.fetchCollectorDetail(collectorId)
            val success = result != null
            PerformanceTracker.endSection("HU06-CollectorDetail(id=$collectorId)", t0, success)
            if (success) {
                _collector.value = result
            } else {
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    companion object {
        var testRepositoryFactory: ((Application) -> ICollectorRepository)? = null

        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = testRepositoryFactory?.invoke(application)
                    ?: CollectorRepository(application)
                CollectorDetailViewModel(repo)
            }
        }
    }
}
