package com.example.library_third_party_sso_java;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.example.library_third_party_sso_java.Pub.GOOGLE_RC_SIGN_IN;
import static com.example.library_third_party_sso_java.Pub.LINE_REQUEST_CODE;

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
    private ThirdPartySSOCallback mThirdPartySSOCallback;

    //------------Google------------//
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    //------------Google------------//
    //------------FB------------//
    private CallbackManager mCallbackManager;
    private UserData mUserData;
    //------------FB------------//
    //------------Line------------//
    //------------Line------------//

    public static ThirdPartySSOController newInstance(Activity activity, ThirdPartySSOCallback thirdPartySSOCallback) {
        if (mThirdPartySSOController == null) {
            mThirdPartySSOController = new ThirdPartySSOController(activity);
            mThirdPartySSOController.init(thirdPartySSOCallback);
        }
        return mThirdPartySSOController;
    }

    public ThirdPartySSOController(Activity activity) {
        mActivity = activity;
        //------------Google------------//
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, mGoogleSignInOptions);
        //------------FB------------//
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void init(ThirdPartySSOCallback thirdPartySSOCallback) {
        mThirdPartySSOCallback = thirdPartySSOCallback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //FB
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //GOOGLE
        if (requestCode == GOOGLE_RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mUserData = new UserData();
                mUserData.setId(account.getId());
                mUserData.setToken(account.getIdToken());
                mUserData.setEmail(account.getEmail());
                mThirdPartySSOCallback.updateView(mUserData);

            } catch (ApiException e) {
                // GoogleLogin Sign In failed, update UI appropriately
                Log.e("TAG", "GoogleLogin sign in failed", e);
                // ...
            }
        }

        //Line
        if (requestCode == LINE_REQUEST_CODE) {
            LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
            switch (result.getResponseCode()) {

                case SUCCESS:
                    // Login successful
                    String accessToken = result.getLineCredential().getAccessToken().getTokenString();

                    mUserData = new UserData();
                    mUserData.setToken(accessToken);
                    mUserData.setId(result.getLineProfile().getUserId());

                    mThirdPartySSOCallback.updateView(mUserData);
                    break;

                case CANCEL:
                    // Login canceled by user
                    Log.e("ERROR", "LINE Login Canceled by user.");
                    break;

                default:
                    // Login canceled due to other error
                    Log.e("ERROR", "Login FAILED!");
                    Log.e("ERROR", result.getErrorData().toString());
            }
        }
    }

    //------------Google------------//
    public void onGoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, GOOGLE_RC_SIGN_IN);
    }
    //------------Google------------//
    //------------FB------------//

    public void onFaceBookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mUserData = new UserData();
                mUserData.setToken(loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            mUserData.setId(object.optString("id"));
                            mUserData.setCover(object.optString("cover"));
                            mUserData.setName(object.optString("name"));
                            mUserData.setFirstName(object.optString("first_name"));
                            mUserData.setLastName(object.optString("last_name"));
                            mUserData.setAgeRange(object.optString("age_range"));
                            mUserData.setLink(object.optString("link"));
                            mUserData.setGender(object.optString("gender"));
                            mUserData.setLocale(object.optString("locale"));
                            mUserData.setPicture(object.optString("picture"));
                            mUserData.setTimezone(object.optString("timezone"));
                            mUserData.setUpdatedTime(object.optString("updated_time"));
                            mUserData.setVerified(object.optString("verified"));
                            mUserData.setEmail(object.optString("email"));
                            mThirdPartySSOCallback.updateView(mUserData);


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

    //------------FB------------//
    //------------Line------------//]
    public void onLineLogin() {
        try {
            // App-to-app login
            Intent loginIntent = LineLoginApi.getLoginIntent(
                    mActivity,
                    mActivity.getString(R.string.line_channel_id),
                    new LineAuthenticationParams.Builder()
                            .scopes(Arrays.asList(Scope.PROFILE))
                            // .nonce("<a randomly-generated string>") // nonce can be used to improve security
                            .build());
            mActivity.startActivityForResult(loginIntent, LINE_REQUEST_CODE);

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }
    //------------Line------------//

    public void onLogOut() {
        mGoogleSignInClient.signOut();
        LoginManager.getInstance().logOut();
        mThirdPartySSOCallback.updateView(null);
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
