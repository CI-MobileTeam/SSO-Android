package com.example.library_third_party_sso_kotlin

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

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

        fun getHashKey(activity: FragmentActivity): String? {
            try {
                val info = activity.packageManager.getPackageInfo(activity.packageName, PackageManager.GET_SIGNATURES)
                for (signature in info.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyResult = String(Base64.encode(md.digest(), 0))
                    Log.e("TAG", "hash key = ${keyResult}")
                    Toast.makeText(activity, "My FB Key is \n ${keyResult}" , Toast.LENGTH_LONG).show()
                    return keyResult
                }
            } catch (e: Exception) {
                Log.e("TAG", "exception: $e")
                return e.message
            }

            return null
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