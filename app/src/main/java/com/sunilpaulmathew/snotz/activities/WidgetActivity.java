package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.fragments.WidgetFragment;
import com.sunilpaulmathew.snotz.interfaces.AuthenticatorInterface;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.util.concurrent.Executor;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class WidgetActivity extends AppCompatActivity {

    public WidgetActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_main);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WidgetFragment()).commit();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                sCommonUtils.toast(getString(R.string.authentication_failed), WidgetActivity.this).show();
            }
        });

        if (Utils.isFingerprintAvailable(this) && sCommonUtils.getBoolean("use_biometric", false, this)) {
            mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
        } else if (Security.isPINEnabled(this)) {
            new AuthenticatorInterface(true, getString(R.string.authenticate), this) {

                @Override
                public void positiveButtonLister(Editable authText) {
                    if (authText != null && authText.toString().trim().length() == 4
                            && authText.toString().trim().equals(Security.getPIN(WidgetActivity.this))) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WidgetFragment()).commit();
                    }
                }
            }.show();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WidgetFragment()).commit();
        }

    }

}