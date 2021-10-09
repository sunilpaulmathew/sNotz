package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.AsyncTasks;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2020
 */
public class StartActivity extends AppCompatActivity {

    private AppCompatImageView mAppLogo;
    private BiometricPrompt mBiometricPrompt;
    private MaterialTextView mAuthenticationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAppLogo = findViewById(R.id.app_logo);
        mAuthenticationStatus = findViewById(R.id.authentication_status);

        load(mBiometricPrompt, this).execute();

        Executor executor = ContextCompat.getMainExecutor(this);
        mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
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

        Utils.showBiometricPrompt(this);
    }

    private static AsyncTasks load(BiometricPrompt biometricPrompt, Activity activity) {
        return new AsyncTasks() {

            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                // Migrate notes into new format
                if (Utils.exist(activity.getFilesDir().getPath() + "/snotz")) {
                    sNotzUtils.reOrganizeNotes(activity);
                }
            }

            @Override
            public void onPostExecute() {
                if (Utils.getBoolean("use_biometric", false, activity)) {
                    biometricPrompt.authenticate(Utils.showBiometricPrompt(activity));
                } else if (Security.isPINEnabled(activity)) {
                    Security.authenticate(activity);
                } else {
                    // Launch MainActivity
                    Intent mainActivity = new Intent(activity, MainActivity.class);
                    activity.startActivity(mainActivity);
                    activity.finish();
                }
            }
        };
    }

}