package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.library_third_party_login.R
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback as FacebookCallback


internal class FacebookLogin(private val activity: FragmentActivity, private val resultCallback: LoginResultCallback) : IThirdPartyLogin {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    init {
        Log.e("FacebookLogin", "facebook_app_id: ${activity.getString(R.string.facebook_app_id)}")
        Log.e("FacebookLogin", "fb_login_protocol_scheme: ${activity.getString(R.string.fb_login_protocol_scheme)}")

        if (checkLocalPropertiesIsNotEmpty()) {
            LoginManager.getInstance().registerCallback(
                    callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult?) {
                            Log.e("FacebookLogin", "token: ${result?.accessToken?.token} + \nuserId: ${result?.accessToken?.userId}")
                            resultCallback.onSuccess(result?.accessToken?.token)
                            Log.e("FacebookLogin", "${result?.recentlyGrantedPermissions}")
                            val profile = Profile.getCurrentProfile()
                            Log.e("FacebookLogin", profile?.id + "/" + profile?.name)
                        }

                        override fun onCancel() {
                            Log.e("FacebookLogin", "onCancel")
                        }

                        override fun onError(error: FacebookException?) {
                            Log.e("FacebookLogin", "onError: ${error?.message}")
                            Toast.makeText(activity, error?.message, Toast.LENGTH_SHORT).show()
                        }

                    }
            )
        }
    }

    override fun logIn() {
        if (checkLocalPropertiesIsNotEmpty()) {
            Log.e("FacebookLogin", "isActive: ${AccessToken.isCurrentAccessTokenActive()}")
            Log.e("FacebookLogin", "isExpired: ${AccessToken.getCurrentAccessToken()?.isExpired} / " +
                    "token is null: ${AccessToken.getCurrentAccessToken() == null}")
            val profile = Profile.getCurrentProfile()
            Log.e("FacebookLogin", profile?.id + "/" + profile?.name)
//        if (!isLogin())
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
        }
    }

    override fun logOut() {
        if (checkLocalPropertiesIsNotEmpty()) {
            Log.e("FacebookLogin", "isActive: ${AccessToken.isCurrentAccessTokenActive()}")
            Log.e("FacebookLogin", "isExpired: ${AccessToken.getCurrentAccessToken()?.isExpired} / " +
                    "token is null: ${AccessToken.getCurrentAccessToken() == null}")
            if (isLogin()) {
                LoginManager.getInstance().logOut()
                Log.e("FacebookLogin", "isActive: ${AccessToken.isCurrentAccessTokenActive()}")
                Log.e("FacebookLogin", "isExpired: ${AccessToken.getCurrentAccessToken()?.isExpired} / " +
                        "token is null: ${AccessToken.getCurrentAccessToken() == null}")
                val profile = Profile.getCurrentProfile()
                Log.e("FacebookLogin", profile?.id + "/" + profile?.name)
            }
        }
    }

    override fun isLogin(): Boolean {
        return if (checkLocalPropertiesIsNotEmpty()) {
            Log.e("FacebookLogin", "isCurrentAccessTokenActive: " + AccessToken.isCurrentAccessTokenActive())
            AccessToken.isCurrentAccessTokenActive()
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (checkLocalPropertiesIsNotEmpty()) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkLocalPropertiesIsNotEmpty(): Boolean {
        return if (activity.getString(R.string.facebook_app_id).isNotEmpty() &&
                activity.getString(R.string.fb_login_protocol_scheme).isNotEmpty()) {
            true
        } else {
            Toast.makeText(activity, "facebook_app_id or fb_login_protocol_scheme is empty", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
