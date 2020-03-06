package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.library_third_party_login.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException


internal class GoogleLogin(private var activity: FragmentActivity, private val resultCallback: LoginResultCallback) : IThirdPartyLogin {
    private var googleSignInClient: GoogleSignInClient? = null

    companion object {
        const val RC_SIGN_IN = 99
    }

    init {
        Log.e("GoogleLogin", "google_login_client_id: ${activity.getString(R.string.google_login_client_id)}")
        if (checkLocalPropertiesIsNotEmpty()) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(activity.getString(R.string.google_login_client_id))
                    .build()
            // Build a GoogleSignInClient with the options specified by gso.
            googleSignInClient = GoogleSignIn.getClient(activity, gso)
        }
    }

    override fun logIn() {
        if (checkLocalPropertiesIsNotEmpty()) {
            //        if (!isLogin())
            activity.startActivityForResult(googleSignInClient?.signInIntent, RC_SIGN_IN)

        }
    }

    override fun logOut() {
        if (checkLocalPropertiesIsNotEmpty()) {
            if (isLogin())
                googleSignInClient?.signOut()
        }
    }

    override fun isLogin(): Boolean {
        return if (checkLocalPropertiesIsNotEmpty()) {
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            val account = GoogleSignIn.getLastSignedInAccount(activity)
            Log.e("GoogleLogin", "isLogin: ${account != null}")
            Log.e("GoogleLogin", account?.email + "/" +
                    account?.displayName + "/" +
                    account?.id + "/" +
                    account?.idToken)
            return account != null
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (checkLocalPropertiesIsNotEmpty()) {
            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    // Signed in successfully, show authenticated UI.
                    Log.e("GoogleLogin", account?.idToken)
                    resultCallback.onSuccess(account?.email + "/" +
                            account?.displayName + "/" +
                            account?.id + "/" +
                            account?.idToken)
                } catch (e: ApiException) {
                    // The ApiException status code indicates the detailed failure reason.
                    // Please refer to the GoogleSignInStatusCodes class reference for more information.
                    Log.e("GoogleLogin", "signInResult:failed code: " + e.statusCode)
                    Toast.makeText(activity, "Please refer to the GoogleSignInStatusCodes class reference for more information => code: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkLocalPropertiesIsNotEmpty(): Boolean {
        return if (activity.getString(R.string.google_login_client_id).isNotEmpty()) {
            true
        } else {
            Toast.makeText(activity, "google_login_client_id is empty", Toast.LENGTH_SHORT).show()
            false
        }
    }

}