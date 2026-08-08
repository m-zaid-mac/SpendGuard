package com.mohammadzaid.spendguard.ui.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

/**
 * Gatekeeps app launch behind device biometrics (or device PIN/pattern as a
 * fallback, via BIOMETRIC_WEAK | DEVICE_CREDENTIAL). Ramp's product sits on
 * top of every dollar a business spends, so "can someone who picks up an
 * unlocked phone see the transaction feed" is a real question — this is the
 * cheapest correct answer to it. Financial data itself is never stored
 * outside Room's app-private database, which is sandboxed by the OS.
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(activity)
        val result = manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailure(errString.toString())
            }

            override fun onAuthenticationFailed() {
                onFailure("Fingerprint or face not recognized")
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock SpendGuard")
            .setSubtitle("Verify it's you before viewing company spend")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        prompt.authenticate(promptInfo)
    }
}
