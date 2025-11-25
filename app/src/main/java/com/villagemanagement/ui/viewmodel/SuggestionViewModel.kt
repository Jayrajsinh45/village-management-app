package com.villagemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villagemanagement.data.model.Suggestion
import com.villagemanagement.data.repository.SuggestionRepository
import com.villagemanagement.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuggestionState(
    val isLoading: Boolean = false,
    val suggestions: List<Suggestion> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val suggestionRepository: SuggestionRepository
) : ViewModel() {

    private val _suggestionState = MutableStateFlow(SuggestionState())
    val suggestionState: StateFlow<SuggestionState> = _suggestionState.asStateFlow()

    fun loadSuggestions(villageId: String) {
        viewModelScope.launch {
            suggestionRepository.observeSuggestionsByVillage(villageId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _suggestionState.value = _suggestionState.value.copy(
                            isLoading = false,
                            suggestions = result.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _suggestionState.value = _suggestionState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _suggestionState.value = _suggestionState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun createSuggestion(suggestion: Suggestion, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _suggestionState.value = _suggestionState.value.copy(isLoading = true)
            when (val result = suggestionRepository.createSuggestion(suggestion)) {
                is Resource.Success -> {
                    _suggestionState.value = _suggestionState.value.copy(isLoading = false)
                    onComplete(true)
                }
                is Resource.Error -> {
                    _suggestionState.value = _suggestionState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    onComplete(false)
                }
                is Resource.Loading -> {
                    _suggestionState.value = _suggestionState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _suggestionState.value = _suggestionState.value.copy(error = null)
    }
}
