package com.sip.guardian.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.sip.guardian.domain.model.User
import com.sip.guardian.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val loggedInUser: User? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Username and password required")
            return
        }
        _uiState.value = LoginUiState(loading = true)
        try {
            val user = loginUseCase.execute(username, password)
            _uiState.value = LoginUiState(loggedInUser = user)
        } catch (e: SecurityException) {
            _uiState.value = LoginUiState(error = e.message ?: "Authentication failed")
        } catch (e: Exception) {
            _uiState.value = LoginUiState(error = "Network error. Check backend URL.")
        }
    }
}
