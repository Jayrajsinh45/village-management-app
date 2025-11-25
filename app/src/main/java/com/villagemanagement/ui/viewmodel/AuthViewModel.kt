package com.villagemanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villagemanagement.data.model.User
import com.villagemanagement.data.model.UserRole
import com.villagemanagement.data.repository.AuthRepository
import com.villagemanagement.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    /**
     * Check if user is already logged in
     */
    private fun checkAuthStatus() {
        if (authRepository.isUserLoggedIn()) {
            viewModelScope.launch {
                when (val result = authRepository.getCurrentUserData()) {
                    is Resource.Success -> {
                        _authState.value = AuthState(
                            isAuthenticated = true,
                            user = result.data
                        )
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                }
            }
        }
    }

    /**
     * Register new user
     */
    fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: UserRole = UserRole.RESIDENT,
        villageId: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.registerUser(email, password, name, phone, role, villageId)) {
                is Resource.Success -> {
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        user = result.data
                    )
                }
                is Resource.Error -> {
                    _authState.value = AuthState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }
    }

    /**
     * Login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.loginUser(email, password)) {
                is Resource.Success -> {
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        user = result.data
                    )
                }
                is Resource.Error -> {
                    _authState.value = AuthState(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }
    }

    /**
     * Logout user
     */
    fun logout() {
        authRepository.logout()
        _authState.value = AuthState()
    }

    /**
     * Update user profile
     */
    fun updateProfile(name: String? = null, phone: String? = null, photoUrl: String? = null) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.updateUserProfile(name, phone, photoUrl)) {
                is Resource.Success -> {
                    // Refresh user data
                    checkAuthStatus()
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Send password reset email
     */
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is Resource.Success -> {
                    onComplete(true, "Password reset email sent")
                }
                is Resource.Error -> {
                    onComplete(false, result.message)
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}
