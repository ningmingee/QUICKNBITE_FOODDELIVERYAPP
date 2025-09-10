package com.example.quicknbiteapp.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quicknbiteapp.data.UserTypeSelectState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Timestamp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val context = application.applicationContext

    private var userTypeForSocialLogin: String = "customer"
    private var credentialManager: CredentialManager? = null

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val userId: String, val userType: String, val isSignup: Boolean = false) : AuthState()
        data class Error(val message: String) : AuthState()
        data class UserData(val user: FirebaseUser?, val userType: String?) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _userType = MutableStateFlow<String?>(null)
    val userType: StateFlow<String?> = _userType

    private val _uiState = MutableStateFlow<UserTypeSelectState>(UserTypeSelectState.MainScreen)
    val uiState: StateFlow<UserTypeSelectState> = _uiState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            if (firebaseAuth.currentUser != null) {
                fetchUserTypeFromFirestore(firebaseAuth.currentUser!!.uid)
            } else {
                _userType.value = null
            }
        }
    }

    // Initialize credential manager with activity context
    fun initializeCredentialManager(context: Context) {
        credentialManager = CredentialManager.Companion.create(context)
    }

    // Handle Google Sign-In with ID token
    fun signInWithGoogle(idToken: String, userType: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    // Use the stored user type from social login
                    val existingUserType = fetchUserTypeFromFirestoreAndWait(user.uid)
                    if (existingUserType == null) {
                        // Save with the user type selected before login
                        saveUserTypeToFirestore(user.uid, userType)
                        _authState.value = AuthState.Success(user.uid, userType, isSignup = true)
                    } else {
                        _authState.value = AuthState.Success(user.uid, existingUserType)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase auth with Google failed: ${e.message}")
                _authState.value = AuthState.Error("Google authentication failed: ${e.localizedMessage}")
            }
        }
    }

    // Handle the credential response from Google Sign-In
    private fun handleSignIn(credential: Credential) {
        viewModelScope.launch {
            try {
                // Check if credential is of type Google ID
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    // Create Google ID Token credential from the data
                    val googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
                    // Sign in to Firebase using the token
                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                } else {
                    _authState.value = AuthState.Error("Invalid credential type")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Handle sign-in error: ${e.message}")
                _authState.value = AuthState.Error("Failed to process credentials")
            }
        }
    }

    // Authenticate with Firebase using Google ID token
    private suspend fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                // Use the stored user type from social login
                val existingUserType = fetchUserTypeFromFirestoreAndWait(user.uid)
                if (existingUserType == null) {
                    // Save with the user type selected before login
                    saveUserTypeToFirestore(user.uid, userTypeForSocialLogin)
                    _authState.value = AuthState.Success(user.uid, userTypeForSocialLogin, isSignup = true)
                } else {
                    _authState.value = AuthState.Success(user.uid, existingUserType)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase auth with Google failed: ${e.message}")
            _authState.value = AuthState.Error("Google authentication failed: ${e.localizedMessage}")
        }
    }

    // User Login
    fun loginUser(email: String, password: String, userType: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    val fetchedUserType = fetchUserTypeFromFirestoreAndWait(user.uid)
                    if (fetchedUserType == userType) {
                        _authState.value = AuthState.Success(user.uid, userType)
                    } else {
                        auth.signOut()
                        _authState.value = AuthState.Error("Login failed: User type mismatch")
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
                    else -> "Login failed: ${e.localizedMessage}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    // User register
    fun registerUser(email: String, password: String, userType: String, userName: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    // Update profile with name if provided
                    userName?.let { name ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user.updateProfile(profileUpdates).await()
                    }

                    // Save user type to Firestore
                    saveUserTypeToFirestore(user.uid, userType)
                    _authState.value = AuthState.Success(user.uid, userType, isSignup = true)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Password is too weak (min 8 characters)"
                    is FirebaseAuthUserCollisionException -> "Email already exists"
                    else -> "Registration failed: ${e.localizedMessage}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    private suspend fun saveUserTypeToFirestore(userId: String, userType: String) {
        try {
            val userData = hashMapOf(
                "userId" to userId,
                "userType" to userType,
                "email" to auth.currentUser?.email,
                "displayName" to auth.currentUser?.displayName,
                "createdAt" to Timestamp.Companion.now()
            )

            db.collection("users").document(userId)
                .set(userData)
                .await()
        } catch (e: Exception) {
            println("Error saving user type to Firestore: ${e.message}")
            throw e
        }
    }

    private suspend fun fetchUserTypeFromFirestoreAndWait(userId: String): String? {
        return try {
            val document = db.collection("users").document(userId).get().await()
            document.getString("userType")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user type from Firestore: ${e.message}")
            null
        }
    }

    private fun fetchUserTypeFromFirestore(userId: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("users").document(userId).get().await()
                _userType.value = document.getString("userType")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user type from Firestore: ${e.message}")
            }
        }
    }

    fun signInWithFacebook(token: String, userType: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = FacebookAuthProvider.getCredential(token)
                val result = auth.signInWithCredential(credential).await()
                val user = result.user

                if (user != null) {
                    val existingUserType = fetchUserTypeFromFirestoreAndWait(user.uid)
                    if (existingUserType == null) {
                        saveUserTypeToFirestore(user.uid, userType)
                        _authState.value = AuthState.Success(user.uid, userType, isSignup = true)
                    } else {
                        _authState.value = AuthState.Success(user.uid, existingUserType)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Facebook sign-in failed: ${e.message}")
                _authState.value = AuthState.Error("Facebook sign-in failed: ${e.localizedMessage}")
            }
        }
    }

    fun showTermsConditions() {
        _uiState.value = UserTypeSelectState.TermsConditions
    }

    fun showPrivacyPolicy() {
        _uiState.value = UserTypeSelectState.PrivacyPolicy
    }

    fun showVendorAgreement() {
        _uiState.value = UserTypeSelectState.VendorAgreement
    }

    fun navigateBack() {
        _uiState.value = UserTypeSelectState.MainScreen
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getUserPhotoUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }

    fun signOut() {
        // Clear credential state as recommended
        viewModelScope.launch {
            try {
                auth.signOut()
                _currentUser.value = null
                _userType.value = null
                _authState.value = AuthState.Idle
                if (credentialManager != null) {
                    val clearRequest = ClearCredentialStateRequest()
                    credentialManager!!.clearCredentialState(clearRequest)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Logout failed: ${e.message}")
                throw e
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = auth.currentUser
                val userId = user?.uid

                if (user != null && userId != null) {
                    // Delete user data from Firestore first
                    db.collection("users").document(userId).delete().await()

                    // Delete Firebase Auth account
                    user.delete().await()

                    _authState.value = AuthState.Success("", "account_deleted")
                } else {
                    _authState.value = AuthState.Error("No user logged in")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Account deletion failed: ${e.message}")
                _authState.value = AuthState.Error("Failed to delete account: ${e.message}")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.Success("", "reset_email")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Failed to send reset email"
                )
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = auth.currentUser
                if (user != null) {
                    user.updatePassword(newPassword).await()
                    _authState.value = AuthState.Success(user.uid, "password_updated")
                } else {
                    _authState.value = AuthState.Error("No user logged in")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "Failed to update password"
                )
            }
        }
    }

    fun updateProfile(displayName: String?, photoUrl: String?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = auth.currentUser
                if (user != null) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName?.let { this.displayName = it }
                        photoUrl?.let { this.photoUri = Uri.parse(it) }
                    }
                    user.updateProfile(profileUpdates).await()
                    _authState.value = AuthState.Success(user.uid, "profile_updated")
                } else {
                    _authState.value = AuthState.Error("No user logged in")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to update profile: ${e.message}")
            }
        }
    }

    fun getAuthErrorMessage(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is FirebaseAuthUserCollisionException -> "Email already exists"
            is FirebaseAuthInvalidUserException -> "User not found"
            else -> "Authentication failed: ${exception.message}"
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}