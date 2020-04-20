package com.example.third_party_sso_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.library_third_party_sso_google_java.GoogleUserData;
import com.example.library_third_party_sso_google_java.ThirdPartySSOGoogleCallback;
import com.example.library_third_party_sso_google_java.ThirdPartySSOGoogleController;
import com.example.library_third_party_sso_java.ThirdPartySSOCallback;
import com.example.library_third_party_sso_java.ThirdPartySSOController;
import com.example.library_third_party_sso_java.UserData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ThirdPartySSOCallback , ThirdPartySSOGoogleCallback {

    private ThirdPartySSOController mThirdPartySSOController;
    private ThirdPartySSOGoogleController mThirdPartySSOGoogleController;
    private Button mGoogleButton;
    private Button mFaceBookButton;
    private Button mLineButton;
    private Button mLogOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mThirdPartySSOController = ThirdPartySSOController.newInstance(this, this);
        mThirdPartySSOGoogleController = ThirdPartySSOGoogleController.newInstance(this, this);
        mThirdPartySSOController.getHashKey();
        initView();
        initListener();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        mGoogleButton = findViewById(R.id.google_login);
        mFaceBookButton = findViewById(R.id.fb_login);
        mLineButton = findViewById(R.id.line_login);
        mLogOutButton = findViewById(R.id.log_out);
    }

    private void initListener() {
        mGoogleButton.setOnClickListener(this);
        mFaceBookButton.setOnClickListener(this);
        mLineButton.setOnClickListener(this);
        mLogOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_login:
                mThirdPartySSOGoogleController.onGoogleLogin();
                break;
            case R.id.fb_login:
                mThirdPartySSOController.onFaceBookLogin();
                break;
            case R.id.line_login:
                mThirdPartySSOController.onLineLogin();
                break;
            case R.id.log_out:
                mThirdPartySSOController.onLogOut();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mThirdPartySSOController.onActivityResult(requestCode, resultCode, data);
        mThirdPartySSOGoogleController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateView(UserData userData) {
        TextView idTextView = findViewById(R.id.id_text);
        TextView tokenTextView = findViewById(R.id.token_text);
        TextView emailTextView = findViewById(R.id.email_text);
        if (userData == null) {
            idTextView.setText("");
            tokenTextView.setText("");
            emailTextView.setText("");
            return;
        }
        idTextView.setText(userData.getId());

        tokenTextView.setText(userData.getToken());

        emailTextView.setText(userData.getEmail());

    }

    @Override
    public void getGoogleUserData(GoogleUserData googleUserData) {
        if(googleUserData == null)return;
        Log.e("TAG","getGoogleUserData = " +googleUserData.getAccessToken());
        Log.e("TAG","getGoogleUserData = " +googleUserData.getRefreshToken());
    }
}
