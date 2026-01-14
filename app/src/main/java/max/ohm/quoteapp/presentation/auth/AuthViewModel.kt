package max.ohm.quoteapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import max.ohm.quoteapp.domain.model.User
import max.ohm.quoteapp.domain.repository.AuthRepository
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val displayNameError: String? = null,
    val resetEmailSent: Boolean = false
)

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
    data object NavigateToLogin : AuthEvent
    data class ShowMessage(val message: String) : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()
    
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, error = null) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, error = null) }
    }
    
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, error = null) }
    }
    
    fun updateDisplayName(displayName: String) {
        _uiState.update { it.copy(displayName = displayName, displayNameError = null, error = null) }
    }
    
    fun signIn() {
        val state = _uiState.value
        
        // Validate
        var hasError = false
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.signIn(state.email, state.password)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Sign in failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun signUp() {
        val state = _uiState.value
        
        // Validate
        var hasError = false
        if (state.displayName.isBlank()) {
            _uiState.update { it.copy(displayNameError = "Please enter your name") }
            hasError = true
        }
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords don't match") }
            hasError = true
        }
        if (hasError) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.signUp(state.email, state.password, state.displayName)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AuthEvent.ShowMessage("Account created! Please check your email to verify."))
                    _events.emit(AuthEvent.NavigateToLogin)
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Sign up failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun resetPassword() {
        val state = _uiState.value
        
        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Please enter a valid email") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = authRepository.resetPassword(state.email)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            resetEmailSent = true
                        ) 
                    }
                    _events.emit(AuthEvent.ShowMessage("Password reset email sent!"))
                }
                is Resource.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = result.message ?: "Password reset failed"
                        ) 
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }
    
    fun clearState() {
        _uiState.update { AuthUiState() }
    }
}
