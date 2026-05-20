package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepo: IAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        authRepo.signInWithEmail(email, password)
            .onSuccess { _uiState.value = AuthUiState.Idle }
            .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Erro ao entrar") }
    }

    fun signUp(email: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        authRepo.signUp(email, password)
            .onSuccess { _uiState.value = AuthUiState.Idle }
            .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Erro ao criar conta") }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
