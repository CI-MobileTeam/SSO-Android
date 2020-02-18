package com.example.library_third_party_sso_java;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/****************************************************
 * Copyright (C) Alan Corporation. All rights reserved.
 *
 * Author: AlanLai
 * Create Date: 2020-02-17
 * Usage:
 *
 * Revision History
 * Date         Author           Description
 ****************************************************/

public class ThirdPartySSOController {
    private static ThirdPartySSOController mThirdPartySSOController;
    private Activity mActivity;

    //------------Google------------//
    private GoogleLogin mGoogleLogin;
    //------------Google------------//
    //------------FB------------//
    private FacebookLogin mFacebookLogin;
    private CallbackManager mCallbackManager;
    //------------FB------------//
    //------------Line------------//
    //------------Line------------//

    public static ThirdPartySSOController newInstance(Activity activity) {
        if (mThirdPartySSOController == null) {
            mThirdPartySSOController = new ThirdPartySSOController(activity);
            mThirdPartySSOController.init();
        }
        return mThirdPartySSOController;
    }

    public ThirdPartySSOController(Activity activity) {
        mActivity = activity;
        //Google
        mGoogleLogin = new GoogleLogin(mActivity);
        //FB
        mFacebookLogin = new FacebookLogin(mActivity);
    }

    private void init() {
        //FB
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLogin.setCallbackManager(mCallbackManager);
    }

    public void onStart(){
        //Google
        mGoogleLogin.onStart();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mCallbackManager.onActivityResult(requestCode, resultCode, intent);
        mGoogleLogin.onActivityResult(requestCode, resultCode, intent);
    }

    //------------Google------------//
    public void onGoogleLogin(ThirdPartySSOCallback thirdPartySSOCallback){
        mGoogleLogin.setThirdPartySSOCallback(thirdPartySSOCallback);
        mGoogleLogin.signIn();
    }
    //------------Google------------//


    //------------FB------------//
    public void onFBLogin(ThirdPartySSOCallback thirdPartySSOCallback) {
        mFacebookLogin.setThirdPartySSOCallback(thirdPartySSOCallback);
        mFacebookLogin.facebookLogin();
    }

    public void onFBLogout() {
        mFacebookLogin.facebookLogout();
    }

    public Boolean isFBLogin() {
        return mFacebookLogin.getCurrentAccessToken();
    }
    //------------FB------------//

    public void getHashKey() {
        PackageInfo info;
        try {
            info = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyResult = new String(Base64.encode(md.digest(), 0));//String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("TAG", "hash key = " + KeyResult);
                Toast.makeText(mActivity, "My FB Key is \n" + KeyResult, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("TAG", "name not found" + e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG", "no such an algorithm" + e.toString());
        } catch (Exception e) {
            Log.e("TAG", "exception" + e.toString());
        }
    }
}
