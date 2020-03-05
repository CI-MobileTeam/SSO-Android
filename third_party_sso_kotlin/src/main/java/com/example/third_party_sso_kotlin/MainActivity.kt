package com.example.third_party_sso_kotlin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.library_third_party_sso_kotlin.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener , LoginResultCallback{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fbLogin.setOnClickListener(this)
        googleLogin.setOnClickListener(this)
        lineLogin.setOnClickListener(this)
        islogin.setOnClickListener(this)
        logOut.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ThirdPartyLoginManager.INSTANCE.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            fbLogin.id -> {
                ThirdPartyLoginManager.INSTANCE.setThirdPartyLogin(LOGINTYPE.FACEBOOK, this, this)
                ThirdPartyLoginManager.INSTANCE.logIn()
            }

            googleLogin.id -> {
                ThirdPartyLoginManager.INSTANCE.setThirdPartyLogin(LOGINTYPE.GOOGLE, this, this)
                ThirdPartyLoginManager.INSTANCE.logIn()
            }

            lineLogin.id -> {
                ThirdPartyLoginManager.INSTANCE.setThirdPartyLogin(LOGINTYPE.LINE, this, this)
                ThirdPartyLoginManager.INSTANCE.logIn()
            }

            islogin.id -> {
                ThirdPartyLoginManager.INSTANCE.isLogin()
            }

            logOut.id -> ThirdPartyLoginManager.INSTANCE.logOut()
        }
    }

    override fun onSuccess(token: String?) {
        Toast.makeText(this@MainActivity, token, Toast.LENGTH_LONG).show()
    }
}

