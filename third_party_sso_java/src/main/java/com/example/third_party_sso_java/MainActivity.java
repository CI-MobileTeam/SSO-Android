package com.example.third_party_sso_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.library_third_party_sso_java.facebook.FacebookController;

public class MainActivity extends AppCompatActivity {

    private FacebookController mFacebookController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mFacebookController = FacebookController.newInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookController.onActivityResult(requestCode, resultCode, data);
    }
}
