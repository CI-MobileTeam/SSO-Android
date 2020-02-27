package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.util.Log
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
    private var lineApiClient: LineApiClient

    init {
        loginIntent = LineLoginApi.getLoginIntent(
                activity,
                activity.getString(R.string.line_channel_id),
                LineAuthenticationParams.Builder()
                        .scopes(listOf(Scope.PROFILE))
                        // .nonce("<a randomly-generated string>") // nonce can be used to improve security
                        .build())
        lineApiClient = LineApiClientBuilder(activity.applicationContext, activity.getString(R.string.line_channel_id)).build()
    }

    override fun logIn() {
        try {
            activity.startActivityForResult(loginIntent, REQUEST_CODE)
        } catch (e: Exception) {
            Log.e("LineLogin", "Error: $e")
        }
    }

    override fun logOut() {
        Thread(Runnable {
            lineApiClient.logout()
        }).start()
    }

    override fun isLogin(): Boolean {
        Log.e("LineLogin", "isSuccess: " + lineApiClient.currentAccessToken.isSuccess)
        return lineApiClient.currentAccessToken.isSuccess
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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