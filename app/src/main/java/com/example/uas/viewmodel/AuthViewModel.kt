package com.example.uas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas.data.User
import com.example.uas.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isUserInitialized = MutableStateFlow(false)
    val isUserInitialized: StateFlow<Boolean> = _isUserInitialized.asStateFlow()

    init {
        // Fetch current user details on startup if already logged into Firebase
        if (repository.currentFirebaseUser != null) {
            viewModelScope.launch {
                _currentUser.value = repository.fetchCurrentUser()
                _isUserInitialized.value = true
            }
        } else {
            _isUserInitialized.value = true
        }
    }
    
    fun hasActiveSession(): Boolean {
        return repository.currentFirebaseUser != null
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Boolean {
        val user = repository.register(name, email, phone, password)
        if (user != null) {
            _currentUser.value = user
            return true
        }
        return false
    }

    suspend fun login(email: String, password: String): Boolean {
        val user = repository.login(email, password)
        if (user != null) {
            _currentUser.value = user
            return true
        }
        return false
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Boolean {
        return repository.changePassword(oldPassword, newPassword)
    }

    suspend fun updateProfile(name: String, phone: String): Boolean {
        val updated = repository.updateProfile(name, phone)
        if (updated != null) {
            _currentUser.value = updated
            return true
        }
        return false
    }
}
