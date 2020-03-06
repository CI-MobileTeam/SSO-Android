package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.library_third_party_login.R
import com.linecorp.linesdk.Scope
import com.linecorp.linesdk.auth.LineAuthenticationParams
import com.linecorp.linesdk.auth.LineLoginApi
import com.linecorp.linesdk.LineApiResponseCode
import com.linecorp.linesdk.api.LineApiClient
import com.linecorp.linesdk.api.LineApiClientBuilder


class LineLogin(private val activity: FragmentActivity,private val resultCallback: LoginResultCallback) : IThirdPartyLogin {
    companion object {
        const val REQUEST_CODE = 100
    }

    private var loginIntent: Intent? = null
    private var lineApiClient: LineApiClient? = null

    init {
        Log.e("LineLogin", "line_channel_id: ${activity.getString(R.string.line_channel_id)}")
        if (checkLocalPropertiesIsNotEmpty()) {
            loginIntent = LineLoginApi.getLoginIntent(
                    activity,
                    activity.getString(R.string.line_channel_id),
                    LineAuthenticationParams.Builder()
                            .scopes(listOf(Scope.PROFILE))
                            // .nonce("<a randomly-generated string>") // nonce can be used to improve security
                            .build())
            lineApiClient = LineApiClientBuilder(activity.applicationContext, activity.getString(R.string.line_channel_id)).build()
        }
    }

    override fun logIn() {
        if (checkLocalPropertiesIsNotEmpty()) {
            try {
                activity.startActivityForResult(loginIntent, REQUEST_CODE)
            } catch (e: Exception) {
                Log.e("LineLogin", "Error: $e")
            }
        }
    }

    override fun logOut() {
        if (checkLocalPropertiesIsNotEmpty()) {
            Thread(Runnable {
                lineApiClient?.logout()
            }).start()
        }
    }

    override fun isLogin(): Boolean {
        return if (checkLocalPropertiesIsNotEmpty()) {
            Log.e("LineLogin", "isSuccess: " + lineApiClient?.currentAccessToken?.isSuccess)
            lineApiClient?.currentAccessToken?.isSuccess ?: false
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (checkLocalPropertiesIsNotEmpty()) {
            if (requestCode != REQUEST_CODE) {
                Log.e("LineLogin", "Unsupported Request")
                return
            }

            val result = LineLoginApi.getLoginResultFromIntent(data)

            when (result.responseCode) {

                LineApiResponseCode.SUCCESS -> {
                    // Login successful
                    val accessToken = result.lineCredential?.accessToken?.tokenString
                    Log.e("LineLogin", "accessToken: $accessToken")
                    Log.e("LineLogin", "isSuccess: ${result.isSuccess}")
                    Log.e("LineLogin", "userId: ${result.lineProfile?.userId}")
                    Log.e("LineLogin", "displayName: ${result.lineProfile?.displayName}")
                    Log.e("LineLogin", "statusMessage: ${result.lineProfile?.statusMessage}")
                    Log.e("LineLogin", "pictureUrl: ${result.lineProfile?.pictureUrl}")

                    resultCallback.onSuccess(accessToken)
                }

                LineApiResponseCode.CANCEL ->
                    // Login canceled by user
                    Log.e("LineLogin", "LINE Login Canceled by user.")

                else -> {
                    // Login canceled due to other error
                    Log.e("LineLogin", "Login FAILED!")
                    Log.e("LineLogin", result.errorData.toString())
                }

            }
        }
    }

    private fun checkLocalPropertiesIsNotEmpty(): Boolean {
        return if (activity.getString(R.string.line_channel_id).isNotEmpty()) {
            true
        } else {
            Toast.makeText(activity, "line_channel_id is empty", Toast.LENGTH_SHORT).show()
            false
        }
    }
}