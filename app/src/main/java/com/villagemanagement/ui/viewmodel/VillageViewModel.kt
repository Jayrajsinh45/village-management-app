package com.villagemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villagemanagement.data.model.Village
import com.villagemanagement.data.repository.VillageRepository
import com.villagemanagement.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VillageState(
    val isLoading: Boolean = false,
    val villages: List<Village> = emptyList(),
    val selectedVillage: Village? = null,
    val error: String? = null
)

@HiltViewModel
class VillageViewModel @Inject constructor(
    private val villageRepository: VillageRepository
) : ViewModel() {

    private val _villageState = MutableStateFlow(VillageState())
    val villageState: StateFlow<VillageState> = _villageState.asStateFlow()

    init {
        loadVillages()
    }

    /**
     * Load all villages
     */
    fun loadVillages() {
        viewModelScope.launch {
            _villageState.value = _villageState.value.copy(isLoading = true, error = null)

            when (val result = villageRepository.getAllVillages()) {
                is Resource.Success -> {
                    _villageState.value = VillageState(
                        villages = result.data
                    )
                }
                is Resource.Error -> {
                    _villageState.value = VillageState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _villageState.value = VillageState(isLoading = true)
                }
            }
        }
    }

    /**
     * Observe villages in real-time
     */
    fun observeVillages() {
        viewModelScope.launch {
            villageRepository.observeAllVillages().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _villageState.value = _villageState.value.copy(
                            isLoading = false,
                            villages = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _villageState.value = _villageState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _villageState.value = _villageState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    /**
     * Load specific village
     */
    fun loadVillage(villageId: String) {
        viewModelScope.launch {
            _villageState.value = _villageState.value.copy(isLoading = true, error = null)

            when (val result = villageRepository.getVillageById(villageId)) {
                is Resource.Success -> {
                    _villageState.value = _villageState.value.copy(
                        isLoading = false,
                        selectedVillage = result.data
                    )
                }
                is Resource.Error -> {
                    _villageState.value = _villageState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _villageState.value = _villageState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Create new village
     */
    fun createVillage(village: Village, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _villageState.value = _villageState.value.copy(isLoading = true, error = null)

            when (val result = villageRepository.createVillage(village)) {
                is Resource.Success -> {
                    _villageState.value = _villageState.value.copy(isLoading = false)
                    onComplete(true, result.data)
                    loadVillages() // Refresh list
                }
                is Resource.Error -> {
                    _villageState.value = _villageState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    onComplete(false, result.message)
                }
                is Resource.Loading -> {
                    _villageState.value = _villageState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Update village
     */
    fun updateVillage(villageId: String, updates: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _villageState.value = _villageState.value.copy(isLoading = true, error = null)

            when (val result = villageRepository.updateVillage(villageId, updates)) {
                is Resource.Success -> {
                    _villageState.value = _villageState.value.copy(isLoading = false)
                    onComplete(true)
                    loadVillages() // Refresh list
                }
                is Resource.Error -> {
                    _villageState.value = _villageState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    onComplete(false)
                }
                is Resource.Loading -> {
                    _villageState.value = _villageState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Search villages
     */
    fun searchVillages(query: String) {
        viewModelScope.launch {
            _villageState.value = _villageState.value.copy(isLoading = true, error = null)

            when (val result = villageRepository.searchVillages(query)) {
                is Resource.Success -> {
                    _villageState.value = VillageState(
                        villages = result.data
                    )
                }
                is Resource.Error -> {
                    _villageState.value = VillageState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _villageState.value = VillageState(isLoading = true)
                }
            }
        }
    }

    /**
     * Clear error
     */
    fun clearError() {
        _villageState.value = _villageState.value.copy(error = null)
    }
}
