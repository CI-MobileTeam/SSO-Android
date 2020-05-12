package com.example.library_third_party_sso_google_java;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


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

public class ThirdPartySSOGoogleController {
    private static ThirdPartySSOGoogleController mThirdPartySSOGoogleController;
    private Application mApplication;
    private MutableLiveData<GoogleUserData> mGoogleUserDataLiveData;
    private GoogleUserData mGoogleUserData;
    private OkHttpClient mOkHttpClient;
    private GoogleAuthEntity mGoogleAuthEntity;

    private static final int GOOGLE_RC_SIGN_IN_REQUEST_CODE = 8199;

    //------------Google------------//
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    //------------Google------------//

    public ThirdPartySSOGoogleController(Application application) {
        mApplication = application;
        mGoogleUserDataLiveData = new MutableLiveData<>();
        //------------Google------------//
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(mApplication.getString(R.string.google_login_client_id))
                .requestServerAuthCode(mApplication.getString(R.string.google_login_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mApplication, mGoogleSignInOptions);

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "onActivityResult requestCode = " + requestCode);
        //GOOGLE
        if (requestCode == GOOGLE_RC_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mGoogleUserData = new GoogleUserData();
                mGoogleUserData.setId(account.getId());
                mGoogleUserData.setIdToken(account.getIdToken());
                mGoogleUserData.setEmail(account.getEmail());

                Log.e("TAG", " google client_id =" + mApplication.getString(R.string.google_login_client_id));
                Log.e("TAG", " google client_secret =" + mApplication.getString(R.string.google_client_secret));
                Log.e("TAG", " google ServerAuthCode =" + account.getServerAuthCode());

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", mApplication.getString(R.string.google_login_client_id))
                        .add("client_secret", mApplication.getString(R.string.google_client_secret))
                        .add("redirect_uri", "")
                        .add("code", Objects.requireNonNull(account.getServerAuthCode()))
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.googleapis.com/oauth2/v4/token")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("TAG", " onFailure =" + e.toString());
                        Toast.makeText(mApplication, "GoogleLogin sign in failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            mGoogleAuthEntity = new Gson().fromJson(jsonObject.toString(), new TypeToken<GoogleAuthEntity>() {
                            }.getType());

                            if (mGoogleAuthEntity.getAccessToken() != null)
                                mGoogleUserData.setAccessToken(mGoogleAuthEntity.getAccessToken());
                            if (mGoogleAuthEntity.getRefreshToken() != null)
                                mGoogleUserData.setRefreshToken(mGoogleAuthEntity.getRefreshToken());
                            mGoogleUserDataLiveData.postValue(mGoogleUserData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (ApiException e) {
                // GoogleLogin Sign In failed, update UI appropriately
                Log.e("TAG", "GoogleLogin sign in failed = " + e.getMessage());
                // ...
                Toast.makeText(mApplication, "GoogleLogin sign in failed", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public Intent onGoogleLogin() {
        onLogOut();
        return mGoogleSignInClient.getSignInIntent();
    }

    public int getGoogleRcSignInRequestCode() {
        return GOOGLE_RC_SIGN_IN_REQUEST_CODE;
    }

    public void onLogOut() {
        mGoogleSignInClient.signOut();
        mGoogleUserDataLiveData.postValue(null);
    }

    public MutableLiveData<GoogleUserData> getGoogleUserDataLiveData() {
        return mGoogleUserDataLiveData;
    }

    public void getHashKey() {
        PackageInfo info;
        try {
            info = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String KeyResult = new String(Base64.encode(md.digest(), 0));//String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("TAG", "hash key = " + KeyResult);
                Toast.makeText(mApplication, "My FB Key is \n" + KeyResult, Toast.LENGTH_LONG).show();
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
