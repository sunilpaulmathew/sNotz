package com.sunilpaulmathew.snotz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.sunilpaulmathew.snotz.utils.Utils;

import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2020
 */

public class StartActivity extends AppCompatActivity {

    private AppCompatImageView mAppLogo;
    private AppCompatTextView mAuthenticationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAppLogo = findViewById(R.id.app_logo);
        mAuthenticationStatus = findViewById(R.id.authentication_status);

        new Handler().postDelayed(() -> {
            if (Utils.getBoolean("use_biometric", false, this)) {
                Utils.mBiometricPrompt.authenticate(Utils.mPromptInfo);
            } else {
                // Launch MainActivity
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }, 1000);

        Executor executor = ContextCompat.getMainExecutor(this);
        Utils.mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                mAppLogo.setVisibility(View.GONE);
                finish();
            }
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                mAppLogo.setVisibility(View.GONE);
                // Launch MainActivity
                Intent mainActivity = new Intent(StartActivity.this, MainActivity.class);
                startActivity(mainActivity);
                finish();

            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                mAppLogo.setVisibility(View.GONE);
                mAuthenticationStatus.setTextColor(Color.RED);
                mAuthenticationStatus.setText(getString(R.string.authentication_failed));
                mAuthenticationStatus.setVisibility(View.VISIBLE);
                Utils.showSnackbar(mAuthenticationStatus, getString(R.string.authentication_failed));
            }
        });

        Utils.showBiometricPrompt(this);Utils.showBiometricPrompt(this);
    }

}