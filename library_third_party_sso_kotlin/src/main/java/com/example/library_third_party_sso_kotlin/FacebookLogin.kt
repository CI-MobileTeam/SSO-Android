package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback as FacebookCallback


internal class FacebookLogin(private val activity: FragmentActivity,private val resultCallback: LoginResultCallback) : IThirdPartyLogin {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    init {
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
                    }

                }
        )
    }

    override fun logIn() {
        Log.e("FacebookLogin", "isActive: ${AccessToken.isCurrentAccessTokenActive()}")
        Log.e("FacebookLogin", "isExpired: ${AccessToken.getCurrentAccessToken()?.isExpired} / " +
                "token is null: ${AccessToken.getCurrentAccessToken() == null}")
        val profile = Profile.getCurrentProfile()
        Log.e("FacebookLogin", profile?.id + "/" + profile?.name)
//        if (!isLogin())
            LoginManager.getInstance().logInWithReadPermissions(activity, listOf("public_profile", "email"))
    }

    override fun logOut() {
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

    override fun isLogin(): Boolean {
        Log.e("FacebookLogin", "isCurrentAccessTokenActive: " + AccessToken.isCurrentAccessTokenActive())
        return AccessToken.isCurrentAccessTokenActive()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
