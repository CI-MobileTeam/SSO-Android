package com.example.library_third_party_sso_google_java;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/****************************************************
 * Copyright (C) Alan Corporation. All rights reserved.
 *
 * Author: AlanLai
 * Create Date: 2020/4/20
 * Usage:
 *
 * Revision History
 * Date         Author           Description
 ****************************************************/

public class GoogleAuthEntity implements Serializable {
    @SerializedName("access_token")
    private String mAccessToken;
    @SerializedName("expires_in")
    private int mExpiresIn;
    @SerializedName("refresh_token")
    private String mRefreshToken;
    @SerializedName("scope")
    private String mScope;
    @SerializedName("token_type")
    private String nTokenType;
    @SerializedName("id_token")
    private String nIdToken;

    public String getAccessToken() {
        return mAccessToken;
    }

    public int getExpiresIn() {
        return mExpiresIn;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public String getScope() {
        return mScope;
    }

    public String getnTokenType() {
        return nTokenType;
    }

    public String getnIdToken() {
        return nIdToken;
    }
}
