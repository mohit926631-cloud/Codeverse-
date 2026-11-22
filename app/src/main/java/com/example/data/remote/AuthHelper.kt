package com.example.data.remote

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val AUTH_TAG = "Auth"
private const val WEB_CLIENT_ID_RES_NAME = "default_web_client_id"

/**
 * Resolves the Google Services-generated default_web_client_id string resource dynamically
 * so CI builds without google-services.json still compile cleanly while runtime lookup
 * remains identical whenever google-services.json is bundled.
 */
private fun resolveDefaultWebClientId(context: Context): String? {
    val resId = context.resources.getIdentifier(
        WEB_CLIENT_ID_RES_NAME,
        "string",
        context.packageName
    )
    if (resId == 0) return null
    return try {
        context.getString(resId).takeIf { it.isNotBlank() }
    } catch (_: Exception) {
        null
    }
}

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

/**
 * Attempts silent startup sign-in using a single GetGoogleIdOption request.
 */
fun attemptSilentGoogleSignIn(
    context: Context,
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
    onUnauthenticated: () -> Unit
) {
    if (FirebaseApp.getApps(context).isEmpty()) {
        onUnauthenticated()
        return
    }
    if (Firebase.auth.currentUser != null) {
        onSuccess()
        return
    }
    val clientId = resolveDefaultWebClientId(context)
    if (clientId == null) {
        onUnauthenticated()
        return
    }
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val activity = context.findActivity() ?: run {
                onUnauthenticated()
                return@launch
            }
            val credential = credentialManager.getCredential(activity, request).credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

/**
 * Interactive Google Sign-In using a single GetSignInWithGoogleOption request.
 * Handles GetCredentialCancellationException separately to preserve diagnostics and reset UI.
 */
fun launchInteractiveGoogleSignIn(
    context: Context,
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
    onCancelled: () -> Unit,
    onError: (String) -> Unit
) {
    if (FirebaseApp.getApps(context).isEmpty()) {
        onError("Firebase config (google-services.json) is missing in this build.")
        return
    }
    val clientId = resolveDefaultWebClientId(context)
    if (clientId == null) {
        onError("Google Sign-In configuration missing: default_web_client_id not found. Add google-services.json and rebuild.")
        return
    }

    val activity = context.findActivity()
    if (activity == null) {
        onError("Unable to launch Google Sign-In from current context.")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    scope.launch {
        try {
            val credential = credentialManager.getCredential(activity, request).credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onSuccess()
            } else {
                onError("Unexpected credential type returned by Google Sign-In.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(AUTH_TAG, "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
            onCancelled()
        } catch (e: Exception) {
            Log.e(AUTH_TAG, "Google Sign-In failed", e)
            onError(e.localizedMessage ?: "Sign-in failed. Please try again.")
        }
    }
}

/**
 * Clears Credential Manager state and signs out of Firebase Auth inside NonCancellable
 * so UI teardown does not abort sign-out mid-flight.
 */
fun signOutFromCodeverse(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onComplete: () -> Unit = {}
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(AUTH_TAG, "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                onComplete()
            }
        }
    }
}
