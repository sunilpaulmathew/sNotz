package com.sunilpaulmathew.snotz.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.util.concurrent.Executor;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.ThemeUtils.sThemeUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 19, 2020
 */
public class StartActivity extends AppCompatActivity {

    private AppCompatImageView mAppLogo;
    private MaterialTextView mAuthenticationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        sThemeUtils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAppLogo = findViewById(R.id.app_logo);
        mAuthenticationStatus = findViewById(R.id.authentication_status);

        // Update widgets on app launch
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(
                getApplication(), WidgetProvider.class));
        WidgetProvider mWidgetProvider = new WidgetProvider();
        mWidgetProvider.onUpdate(this, AppWidgetManager.getInstance(this), ids);

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
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
                Security.launchMainActivity(StartActivity.this);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                mAppLogo.setVisibility(View.GONE);
                mAuthenticationStatus.setTextColor(Color.RED);
                mAuthenticationStatus.setText(getString(R.string.authentication_failed));
                mAuthenticationStatus.setVisibility(View.VISIBLE);
                sCommonUtils.snackBar(mAuthenticationStatus, getString(R.string.authentication_failed)).show();
            }
        });

        Utils.showBiometricPrompt(this);

        if (!sCommonUtils.getBoolean("color_customized", false, this)) {
            sCommonUtils.saveBoolean("color_customized", true, this);
            Intent colorCustomizations = new Intent(this, ColorCustomizationsActivity.class);
            startActivity(colorCustomizations);
            finish();
        } else {
            if (Utils.isFingerprintAvailable(this) && sCommonUtils.getBoolean("use_biometric", false, this)) {
                mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
            } else if (Security.isPINEnabled(this)) {
                Security.authenticate(true, null, -1, this);
            } else {
                Security.launchMainActivity(this);
            }
        }
    }

}