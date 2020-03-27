package com.example.library_third_party_sso_fb_java;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

public class ThirdPartySSOFbController {
    private static ThirdPartySSOFbController mThirdPartySSOFbController;
    private Activity mActivity;
    private ThirdPartySSOFbCallback mThirdPartySSOFbCallback;

    private CallbackManager mCallbackManager;
    private UserData mUserData;

    public static ThirdPartySSOFbController newInstance(Activity activity, ThirdPartySSOFbCallback thirdPartySSOCallback) {
        if (mThirdPartySSOFbController == null) {
            mThirdPartySSOFbController = new ThirdPartySSOFbController(activity);
            mThirdPartySSOFbController.init(thirdPartySSOCallback);
        }
        return mThirdPartySSOFbController;
    }

    public ThirdPartySSOFbController(Activity activity) {
        mActivity = activity;
        //------------FB------------//
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void init(ThirdPartySSOFbCallback thirdPartySSOCallback) {
        mThirdPartySSOFbCallback = thirdPartySSOCallback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //FB
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onFaceBookLogin() {
        onLogOut();
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("TAG", "onFaceBookLogin onSuccess");

                mUserData = new UserData();
                mUserData.setToken(loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            mUserData.setId(object.optString("id"));
                            mUserData.setEmail(object.optString("email"));
                            mThirdPartySSOFbCallback.updateView(mUserData);


                        }
                    }
                });

                // 包入你想要取得的資訊 送出 request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, cover, name, first_name, last_name, age_range," +
                        " link, gender, locale, picture, timezone, updated_time, verified, email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void onLogOut() {
        LoginManager.getInstance().logOut();
        mThirdPartySSOFbCallback.updateView(null);
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
