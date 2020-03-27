package com.example.library_third_party_sso_google_java;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.library_third_party_sso_google_java.Pub.GOOGLE_RC_SIGN_IN;

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
    private Activity mActivity;
    private ThirdPartySSOGoogleCallback mThirdPartySSOGoogleCallback;
    private UserData mUserData;

    //------------Google------------//
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    //------------Google------------//
    

    public static ThirdPartySSOGoogleController newInstance(Activity activity, ThirdPartySSOGoogleCallback thirdPartySSOCallback) {
        if (mThirdPartySSOGoogleController == null) {
            mThirdPartySSOGoogleController = new ThirdPartySSOGoogleController(activity);
            mThirdPartySSOGoogleController.init(thirdPartySSOCallback);
        }
        return mThirdPartySSOGoogleController;
    }

    public ThirdPartySSOGoogleController(Activity activity) {
        mActivity = activity;
        //------------Google------------//
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.google_login_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, mGoogleSignInOptions);
    }

    private void init(ThirdPartySSOGoogleCallback thirdPartySSOGoogleCallback) {
        mThirdPartySSOGoogleCallback = thirdPartySSOGoogleCallback;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //GOOGLE
        if (requestCode == GOOGLE_RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mUserData = new UserData();
                mUserData.setId(account.getId());
                mUserData.setToken(account.getIdToken());
                mUserData.setEmail(account.getEmail());
                mThirdPartySSOGoogleCallback.updateView(mUserData);

            } catch (ApiException e) {
                // GoogleLogin Sign In failed, update UI appropriately
                Log.e("TAG", "GoogleLogin sign in failed", e);
                // ...
            }
        }
    }

    //------------Google------------//
    public void onGoogleLogin() {
        onLogOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, GOOGLE_RC_SIGN_IN);
    }
    //------------Google------------//

    public void onLogOut() {
        mGoogleSignInClient.signOut();
        mThirdPartySSOGoogleCallback.updateView(null);
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
