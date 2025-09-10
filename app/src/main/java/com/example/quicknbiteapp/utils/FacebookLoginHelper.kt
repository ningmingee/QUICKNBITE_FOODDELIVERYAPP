package com.example.quicknbiteapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class FacebookLoginHelper(private val context: Context) {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private val loginManager: LoginManager = LoginManager.getInstance()

    private var onSuccess: ((String) -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null


    fun setupCallback(
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit,
        onCancel: () -> Unit
    ) {
        this.onSuccess = onSuccess
        this.onError = onError
        this.onCancel = onCancel

        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "Facebook login successful.")
                val token = result.accessToken.token
                onSuccess(token)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login cancelled.")
                onCancel()
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Facebook login error:", error)
                onError(error)
            }
        })
    }

    fun signIn(activity: Activity) {
        loginManager.logInWithReadPermissions(
            activity,
            listOf("public_profile", "email")
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "FacebookLoginHelper"

    }
}

@Composable
fun rememberFacebookLoginHelper(): FacebookLoginHelper {
    val context = LocalContext.current
    return remember { FacebookLoginHelper(context) }
}