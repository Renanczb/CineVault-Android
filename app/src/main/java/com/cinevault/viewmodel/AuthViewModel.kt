package com.cinevault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinevault.repository.AuthRepository
import com.cinevault.utils.UiState
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _loginState = MutableLiveData<UiState<Unit>>()
    val loginState: LiveData<UiState<Unit>> = _loginState

    private val _registerState = MutableLiveData<UiState<Unit>>()
    val registerState: LiveData<UiState<Unit>> = _registerState

    val isLoggedIn: Boolean get() = repo.currentUser != null

    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            try {
                repo.login(email, password)
                _loginState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        _registerState.value = UiState.Loading
        viewModelScope.launch {
            try {
                repo.register(email, password, name)
                _registerState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _registerState.value = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun logout() = repo.logout()
}
