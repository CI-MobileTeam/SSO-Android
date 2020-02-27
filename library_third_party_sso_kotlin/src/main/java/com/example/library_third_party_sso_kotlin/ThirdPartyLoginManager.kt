package com.example.library_third_party_sso_kotlin

import android.content.Intent
import androidx.fragment.app.FragmentActivity

enum class LOGINTYPE {
    FACEBOOK,
    GOOGLE,
    LINE
}

class ThirdPartyLoginManager : IThirdPartyLogin {

    private var iThirdPartyLogin: IThirdPartyLogin? = null

    fun setThirdPartyLogin(loginType: LOGINTYPE, activity: FragmentActivity, resultCallback: LoginResultCallback) {
        this.iThirdPartyLogin = ThirdPartyLoginFactory.createThirdPartyLogin(loginType, activity, resultCallback)
    }

    fun setThirdPartyLogin(iThirdPartyLogin: IThirdPartyLogin) {
        this.iThirdPartyLogin = iThirdPartyLogin
    }

    override fun logIn() {
        iThirdPartyLogin?.logIn()
    }

    override fun logOut() {
        iThirdPartyLogin?.logOut()
    }

    override fun isLogin(): Boolean {
        return iThirdPartyLogin?.isLogin() ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        iThirdPartyLogin?.onActivityResult(requestCode, resultCode, data)
    }


    companion object {
        val INSTANCE: ThirdPartyLoginManager by lazy {
            ThirdPartyLoginManager()
        }
    }
}

private class ThirdPartyLoginFactory {
    companion object {
        fun createThirdPartyLogin(loginType: LOGINTYPE, activity: FragmentActivity, resultCallback: LoginResultCallback): IThirdPartyLogin {
            return when (loginType) {
                LOGINTYPE.FACEBOOK -> FacebookLogin(activity, resultCallback)
                LOGINTYPE.GOOGLE -> GoogleLogin(activity, resultCallback)
                LOGINTYPE.LINE -> LineLogin(activity, resultCallback)
            }
        }
    }
}