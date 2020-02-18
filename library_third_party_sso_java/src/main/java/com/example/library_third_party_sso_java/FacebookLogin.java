package com.example.library_third_party_sso_java;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

 class FacebookLogin {

    private Activity mActivity;
    private FacebookData mFacebookData;
    private CallbackManager mCallbackManager;

    private ThirdPartySSOCallback mThirdPartySSOCallback;

    public FacebookLogin(Activity activity) {
        mActivity = activity;
        mFacebookData = new FacebookData();
    }

    public void setCallbackManager(CallbackManager callbackManager) {
        mCallbackManager = callbackManager;
    }

    public void setThirdPartySSOCallback(ThirdPartySSOCallback thirdPartySSOCallback) {
        mThirdPartySSOCallback = thirdPartySSOCallback;
    }

    public void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mFacebookData.setToken(loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object != null) {
                            mFacebookData.setId(object.optString("id"));
                            mFacebookData.setCover(object.optString("cover"));
                            mFacebookData.setName(object.optString("name"));
                            mFacebookData.setFirstName(object.optString("first_name"));
                            mFacebookData.setLastName(object.optString("last_name"));
                            mFacebookData.setAgeRange(object.optString("age_range"));
                            mFacebookData.setLink(object.optString("link"));
                            mFacebookData.setGender(object.optString("gender"));
                            mFacebookData.setLocale(object.optString("locale"));
                            mFacebookData.setPicture(object.optString("picture"));
                            mFacebookData.setTimezone(object.optString("timezone"));
                            mFacebookData.setUpdatedTime(object.optString("updated_time"));
                            mFacebookData.setVerified(object.optString("verified"));
                            mFacebookData.setEmail(object.optString("email"));
                            mThirdPartySSOCallback.getFacebookData(mFacebookData);

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

    public void facebookLogout() {

        // 會直接登出
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {

                Log.e(getClass().getName(), "logoutFacebook_onCompleted = " + response.toString());
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    public Boolean getCurrentAccessToken() {
        return AccessToken.isCurrentAccessTokenActive();
    }
}
