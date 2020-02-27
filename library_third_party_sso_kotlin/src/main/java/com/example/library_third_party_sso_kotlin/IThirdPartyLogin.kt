package com.example.library_third_party_sso_kotlin

import android.content.Intent

interface IThirdPartyLogin {
    fun logIn()

    fun logOut()

    fun isLogin(): Boolean

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface LoginResultCallback {
    fun onSuccess(token: String?)
}