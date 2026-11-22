package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.core.firebase.configureAppCheck
import com.example.core.i18n.AppLanguage
import com.example.data.local.LocalPreferences
import com.example.data.local.UserPreferencesStore
import com.example.data.remote.attemptSilentGoogleSignIn
import com.example.data.remote.authStateFlow
import com.example.ui.navigation.AuthenticatedCodeverseShell
import com.example.ui.navigation.CodeverseRoutes
import com.example.ui.screens.onboarding.LanguageSelectionScreen
import com.example.ui.screens.onboarding.LoginRegistrationScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.onboarding.SplashScreen
import com.example.ui.theme.CodeverseTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent BEFORE any UI, ViewModels, or Firebase calls
        configureAppCheck(this, intent)

        // STEP 2: Enable modern Android edge-to-edge layout
        enableEdgeToEdge()

        setContent {
            CodeverseRoot()
        }
    }
}

@Composable
private fun CodeverseRoot() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefsStore = remember(context) { UserPreferencesStore(context) }
    val localPrefs by prefsStore.preferencesFlow.collectAsStateWithLifecycle(initialValue = LocalPreferences())
    val selectedLanguage = AppLanguage.fromCode(localPrefs.languageCode)

    val auth = remember(context) {
        if (FirebaseApp.getApps(context).isNotEmpty()) Firebase.auth else null
    }
    val authFlow = remember(auth) { auth?.authStateFlow() ?: flowOf<FirebaseUser?>(null) }
    val currentUser by authFlow.collectAsStateWithLifecycle(initialValue = auth?.currentUser)
    var splashCompleted by remember { mutableStateOf(false) }

    // Attempt silent Google auto-sign-in once during Splash
    val credentialManager = remember(context) { CredentialManager.create(context) }
    LaunchedEffect(Unit) {
        attemptSilentGoogleSignIn(
            context = context,
            credentialManager = credentialManager,
            scope = scope,
            onSuccess = { /* AuthStateFlow updates currentUser automatically */ },
            onUnauthenticated = { /* Stay on pre-auth onboarding/login flow */ }
        )
    }

    if (!splashCompleted) {
        CodeverseTheme(darkTheme = true) {
            SplashScreen(onFinished = { splashCompleted = true })
        }
        return
    }

    val user = currentUser
    if (user == null) {
        // Unauthenticated pre-auth flow (Screens 2, 3, 4). Zero Firestore listeners run here.
        CodeverseTheme(darkTheme = localPrefs.darkThemeEnabled) {
            val preAuthNav = rememberNavController()
            NavHost(
                navController = preAuthNav,
                startDestination = CodeverseRoutes.ONBOARDING
            ) {
                composable(CodeverseRoutes.ONBOARDING) {
                    OnboardingScreen(
                        onContinue = { preAuthNav.navigate(CodeverseRoutes.LANGUAGE_SELECTION) },
                        onSkipToAuth = { preAuthNav.navigate(CodeverseRoutes.LOGIN_REGISTRATION) }
                    )
                }
                composable(CodeverseRoutes.LANGUAGE_SELECTION) {
                    LanguageSelectionScreen(
                        selectedLanguage = selectedLanguage,
                        onSelectLanguage = { lang ->
                            scope.launch { prefsStore.setLanguage(lang.code) }
                        },
                        onContinue = { preAuthNav.navigate(CodeverseRoutes.LOGIN_REGISTRATION) }
                    )
                }
                composable(CodeverseRoutes.LOGIN_REGISTRATION) {
                    LoginRegistrationScreen(
                        selectedLanguage = selectedLanguage,
                        onAuthenticated = { /* authStateFlow automatically transitions to AuthenticatedCodeverseShell */ },
                        onChangeLanguage = { preAuthNav.navigate(CodeverseRoutes.LANGUAGE_SELECTION) }
                    )
                }
            }
        }
    } else {
        // Authenticated session strictly keyed by user.uid for clean account switch & listener disposal
        key(user.uid) {
            AuthenticatedCodeverseShell(
                currentUserId = user.uid,
                displayName = user.displayName ?: "Codeverse Explorer",
                email = user.email ?: ""
            )
        }
    }
}
