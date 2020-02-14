package com.example.library_third_party_login.facebook;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.library_third_party_login.Callback;
import com.facebook.CallbackManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 　Created by Alan on 2017/8/3.
 */

public class FacebookController {

    private static FacebookController mFacebookController;
    private FacebookLogin mFacebookLogin;
    private Activity mActivity;
    private CallbackManager mCallbackManager;

    public static FacebookController newInstance(Activity activity) {
        if (mFacebookController == null) {
            mFacebookController = new FacebookController(activity);
            mFacebookController.init();
        }
        return mFacebookController;
    }

    public FacebookController(Activity activity) {
        mActivity = activity;
        mFacebookLogin = new FacebookLogin(mActivity);
    }

    public void init() {
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLogin.setCallbackManager(mCallbackManager);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mCallbackManager.onActivityResult(requestCode, resultCode, intent);
    }

    public void onLogin(Callback callback) {
        mFacebookLogin.setCallback(callback);
        mFacebookLogin.facebookLogin();
    }

    public void onLogout() {
        mFacebookLogin.facebookLogout();
    }

    public Boolean isLogin() {
        return mFacebookLogin.getCurrentAccessToken() != null;
    }

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
