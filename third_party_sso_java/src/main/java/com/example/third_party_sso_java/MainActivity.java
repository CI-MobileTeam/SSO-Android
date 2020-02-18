package com.example.third_party_sso_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.library_third_party_sso_java.ThirdPartySSOCallback;
import com.example.library_third_party_sso_java.ThirdPartySSOController;
import com.example.library_third_party_sso_java.FacebookData;
import com.example.library_third_party_sso_java.GoogleData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ThirdPartySSOCallback {

    private ThirdPartySSOController mThirdPartySSOController;
    private Button mGoogleButton;
    private Button mFaceBookButton;
    private Button mLineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mThirdPartySSOController = ThirdPartySSOController.newInstance(this);
        initView();

        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mThirdPartySSOController.onStart();
    }

    private void initView() {
        mGoogleButton = findViewById(R.id.google_login);
        mFaceBookButton = findViewById(R.id.fb_login);
        mLineButton = findViewById(R.id.line_login);
    }

    private void initListener() {
        mGoogleButton.setOnClickListener(this);
        mFaceBookButton.setOnClickListener(this);
        mLineButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.google_login:
                mThirdPartySSOController.onGoogleLogin(this);
                break;
            case R.id.fb_login:
                mThirdPartySSOController.onFBLogin(this);
                break;
            case R.id.line_login:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mThirdPartySSOController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getFacebookData(FacebookData facebookData) {

    }

    @Override
    public void updateGoogleUI(GoogleData googleData) {
        if(googleData != null) {
            Log.e("TAG", "sjew  = " + googleData.getEmail());
        }
    }

}
