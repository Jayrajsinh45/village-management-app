package com.villagemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villagemanagement.data.model.HelpRequest
import com.villagemanagement.data.repository.HelpRepository
import com.villagemanagement.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HelpState(
    val isLoading: Boolean = false,
    val requests: List<HelpRequest> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val helpRepository: HelpRepository
) : ViewModel() {

    private val _helpState = MutableStateFlow(HelpState())
    val helpState: StateFlow<HelpState> = _helpState.asStateFlow()

    fun loadHelpRequests(villageId: String) {
        viewModelScope.launch {
            helpRepository.observeHelpRequestsByVillage(villageId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _helpState.value = _helpState.value.copy(
                            isLoading = false,
                            requests = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _helpState.value = _helpState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _helpState.value = _helpState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun createHelpRequest(request: HelpRequest, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _helpState.value = _helpState.value.copy(isLoading = true)
            when (val result = helpRepository.createHelpRequest(request)) {
                is Resource.Success -> {
                    _helpState.value = _helpState.value.copy(isLoading = false)
                    onComplete(true)
                }
                is Resource.Error -> {
                    _helpState.value = _helpState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    onComplete(false)
                }
                is Resource.Loading -> {
                    _helpState.value = _helpState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _helpState.value = _helpState.value.copy(error = null)
    }
}
