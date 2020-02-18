package com.example.library_third_party_sso_java;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

class GoogleLogin {
    private static final int RC_SIGN_IN = 1293;
    private Activity mActivity;
    private ThirdPartySSOCallback mThirdPartySSOCallback;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;

    public GoogleLogin(Activity activity) {
        mActivity = activity;

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, mGoogleSignInOptions);
    }

    public void onStart() {
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(mActivity);
    }

    public void setThirdPartySSOCallback(ThirdPartySSOCallback thirdPartySSOCallback) {
        mThirdPartySSOCallback = thirdPartySSOCallback;
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mGoogleSignInAccount = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            GoogleData googleData = new GoogleData();

            if (mGoogleSignInAccount != null) {
                googleData.setEmail(mGoogleSignInAccount.getEmail());
            }


            mThirdPartySSOCallback.updateGoogleUI(googleData);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("GoogleLogin", "signInResult:failed code=" + e.getStatusCode());
            mThirdPartySSOCallback.updateGoogleUI(null);
        }
    }
}
