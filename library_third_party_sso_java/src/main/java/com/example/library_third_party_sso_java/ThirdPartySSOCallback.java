package com.example.library_third_party_sso_java;


/**
 * 　Created by Alan on 2017/8/7.
 */

public interface ThirdPartySSOCallback {

    void getFacebookData(FacebookData facebookData);

    void updateGoogleUI(GoogleData googleData);
}
