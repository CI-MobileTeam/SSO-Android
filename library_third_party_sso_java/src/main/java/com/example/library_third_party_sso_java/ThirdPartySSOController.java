package com.example.library_third_party_sso_java;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.example.library_third_party_sso_java.Pub.RC_SIGN_IN;

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
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
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
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // GoogleLogin Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // GoogleLogin Sign In failed, update UI appropriately
                Log.e("TAG", "GoogleLogin sign in failed", e);
                // ...
            }
        }
    }


    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "signInWithCredential:success " );
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserData userData =new UserData();
                            userData.setEmail(user.getEmail());

                            mThirdPartySSOCallback.updateView(userData);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "signInWithCredential:failure", task.getException());
                            mThirdPartySSOCallback.updateView(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e("TAG", "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.e("TAG", "FirebaseUser = "+user.getUid());
                            Log.e("TAG", "FirebaseUser = "+user.getEmail());
                            UserData userData =new UserData();
                            userData.setEmail(user.getEmail());

                            mThirdPartySSOCallback.updateView(userData);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "signInWithCredential:failure", task.getException());

                            mThirdPartySSOCallback.updateView(null);
                        }

                        // ...
                    }
                });
    }

    //------------Google------------//
    public void onGoogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //------------Google------------//
    //------------FB------------//

    public void onFaceBookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("TAG","onFaceBookLogin onSuccess");

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("TAG","onFaceBookLogin onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TAG","onFaceBookLogin onError");
            }
        });
    }
    //------------FB------------//
    //------------Line------------//
    //------------Line------------//

    public void onLogOut(){
        FirebaseAuth.getInstance().signOut();
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
