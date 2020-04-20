package com.example.library_third_party_sso_google_java;

/**
 * 　Created by Alan on 2017/8/4.
 */

public class GoogleUserData {
    private String mId = "";
    private String mEmail = "";
    private String mIdToken = "";
    private String mAccessToken = "";
    private String mRefreshToken = "";

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getIdToken() {
        return mIdToken;
    }

    public void setIdToken(String idToken) {
        mIdToken = idToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }
}
