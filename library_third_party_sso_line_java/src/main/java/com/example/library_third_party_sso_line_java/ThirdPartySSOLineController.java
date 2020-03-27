package com.example.library_third_party_sso_line_java;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

import static com.example.library_third_party_sso_line_java.Pub.LINE_REQUEST_CODE;


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

public class ThirdPartySSOLineController {
    private static ThirdPartySSOLineController mThirdPartySSOLineController;
    private Activity mActivity;
    private ThirdPartySSOLineCallback mThirdPartySSOLineCallback;

    private UserData mUserData;


    public static ThirdPartySSOLineController newInstance(Activity activity, ThirdPartySSOLineCallback thirdPartySSOCallback) {
        if (mThirdPartySSOLineController == null) {
            mThirdPartySSOLineController = new ThirdPartySSOLineController(activity);
            mThirdPartySSOLineController.init(thirdPartySSOCallback);
        }
        return mThirdPartySSOLineController;
    }

    public ThirdPartySSOLineController(Activity activity) {
        mActivity = activity;
    }

    private void init(ThirdPartySSOLineCallback thirdPartySSOCallback) {
        mThirdPartySSOLineCallback = thirdPartySSOCallback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Line
        if (requestCode == LINE_REQUEST_CODE) {
            LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
            switch (result.getResponseCode()) {

                case SUCCESS:
                    // Login successful
                    String accessToken = Objects.requireNonNull(result.getLineCredential()).getAccessToken().getTokenString();

                    mUserData = new UserData();
                    mUserData.setToken(accessToken);
                    mUserData.setId(Objects.requireNonNull(result.getLineProfile()).getUserId());

                    mThirdPartySSOLineCallback.updateView(mUserData);
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
