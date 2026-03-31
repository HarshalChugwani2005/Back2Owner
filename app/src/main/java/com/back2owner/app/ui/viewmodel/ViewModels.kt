package com.back2owner.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.back2owner.app.data.model.Item
import com.back2owner.app.data.model.User
import com.back2owner.app.domain.usecase.CreateClaimUseCase
import com.back2owner.app.domain.usecase.GetFoundItemsUseCase
import com.back2owner.app.domain.usecase.GetLostItemsUseCase
import com.back2owner.app.domain.usecase.GetUserUseCase
import com.back2owner.app.domain.usecase.ReportItemUseCase
import com.back2owner.app.domain.usecase.SignInUseCase
import com.back2owner.app.domain.usecase.SignUpUseCase
import com.back2owner.app.domain.usecase.GetItemByIdUseCase
import com.back2owner.app.data.model.ItemCategory
import com.back2owner.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
//  FeedViewModel
// ─────────────────────────────────────────────────────────────────────────────
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getLostItemsUseCase: GetLostItemsUseCase,
    private val getFoundItemsUseCase: GetFoundItemsUseCase,
) : ViewModel() {

    private val _lostItems = MutableStateFlow<List<Item>>(emptyList())
    val lostItems: StateFlow<List<Item>> = _lostItems.asStateFlow()

    private val _foundItems = MutableStateFlow<List<Item>>(emptyList())
    val foundItems: StateFlow<List<Item>> = _foundItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lostResult = getLostItemsUseCase()
                if (lostResult.isSuccess) {
                    _lostItems.value = lostResult.getOrNull() ?: emptyList()
                } else {
                    _error.value = lostResult.exceptionOrNull()?.message
                }

                val foundResult = getFoundItemsUseCase()
                if (foundResult.isSuccess) {
                    _foundItems.value = foundResult.getOrNull() ?: emptyList()
                } else {
                    _error.value = foundResult.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadItems()
    }

    fun clearError() { _error.value = null }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ReportItemViewModel
// ─────────────────────────────────────────────────────────────────────────────
@HiltViewModel
class ReportItemViewModel @Inject constructor(
    private val reportItemUseCase: ReportItemUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun reportItem(
        title: String,
        description: String,
        category: ItemCategory,
        location: String,
        itemType: String,
        photoBytes: ByteArray,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val reporterId = authRepository.getCurrentUserId()
                    ?: throw Exception("You must be signed in to report an item")

                val result = reportItemUseCase(
                    title, description, category, location, itemType, photoBytes, reporterId
                )
                if (result.isSuccess) {
                    _success.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun resetSuccess() { _success.value = false }
}

// ─────────────────────────────────────────────────────────────────────────────
//  AuthViewModel
// ─────────────────────────────────────────────────────────────────────────────
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null) // null = checking
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.getCurrentUserId() != null
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email and password cannot be empty"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = signInUseCase(email, password)
            if (result.isSuccess) {
                _isLoggedIn.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Sign in failed"
            }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, displayName: String, collegeId: String) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _error.value = "All fields are required"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = signUpUseCase(email, password, displayName, collegeId)
            if (result.isSuccess) {
                _isLoggedIn.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Sign up failed"
            }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _isLoggedIn.value = false
        }
    }

    fun clearError() { _error.value = null }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ProfileViewModel
// ─────────────────────────────────────────────────────────────────────────────
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _signedOut = MutableStateFlow(false)
    val signedOut: StateFlow<Boolean> = _signedOut.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = authRepository.getCurrentUserId()
                if (uid != null) {
                    val result = getUserUseCase(uid)
                    _user.value = result.getOrNull()
                    result.exceptionOrNull()?.let { _error.value = it.message }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _signedOut.value = true
        }
    }

    fun clearError() { _error.value = null }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ClaimDetailViewModel
// ─────────────────────────────────────────────────────────────────────────────
@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val getItemByIdUseCase: GetItemByIdUseCase,
) : ViewModel() {

    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getItemByIdUseCase(itemId)
            if (result.isSuccess) {
                _item.value = result.getOrNull()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class ClaimDetailViewModel @Inject constructor(
    private val createClaimUseCase: CreateClaimUseCase,
    private val authRepository: AuthRepository,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun submitClaim(itemId: String, securityAnswer: String, message: String) {
        if (securityAnswer.isBlank()) {
            _error.value = "Please provide an answer to the security question"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uid = authRepository.getCurrentUserId()
                    ?: throw Exception("You must be signed in to claim an item")
                val user = getUserUseCase(uid).getOrThrow()

                val result = createClaimUseCase(
                    itemId = itemId,
                    claimerId = uid,
                    claimerName = user.displayName,
                    claimerEmail = user.email,
                    securityAnswer = securityAnswer,
                    message = message,
                )
                if (result.isSuccess) {
                    _success.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun resetSuccess() { _success.value = false }
}
