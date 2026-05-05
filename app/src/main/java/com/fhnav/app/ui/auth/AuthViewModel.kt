package com.fhnav.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhnav.app.data.local.datastore.SettingsDataStore
import com.fhnav.app.data.model.User
import com.fhnav.app.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val otpSent: Boolean = false,
    val phone: String = ""
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            loginUseCase.isLoggedIn.collect { loggedIn ->
                _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
            }
        }
        viewModelScope.launch {
            loginUseCase.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, phone = phone)
            loginUseCase.sendOtp(phone)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, otpSent = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "发送验证码失败"
                    )
                }
        }
    }

    fun verifyOtp(code: String) {
        val phone = _uiState.value.phone
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            loginUseCase.verifyOtp(phone, code)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(isLoading = false, user = user, isLoggedIn = true)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "验证码错误"
                    )
                }
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            loginUseCase.updateNickname(nickname)
                .onSuccess { user ->
                    settingsDataStore.setHasCompletedOnboarding(true)
                    _uiState.value = _uiState.value.copy(isLoading = false, user = user)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "更新昵称失败"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun signOut() {
        viewModelScope.launch {
            loginUseCase.signOut()
            _uiState.value = AuthUiState()
        }
    }
}
