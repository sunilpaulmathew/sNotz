package com.sunilpaulmathew.snotz.activities;

import android.os.Bundle;
import android.text.Editable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.fragments.WidgetChecklistsFragment;
import com.sunilpaulmathew.snotz.fragments.WidgetNotesFragment;
import com.sunilpaulmathew.snotz.interfaces.AuthenticatorInterface;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.util.concurrent.Executor;

import in.sunilpaulmathew.sCommon.Adapters.sPagerAdapter;
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
        setContentView(R.layout.activity_widget);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        MaterialTextView mCancel = findViewById(R.id.cancel);
        TabLayout mTabLayout = findViewById(R.id.tab_Layout);
        ViewPager mViewPager = findViewById(R.id.view_pager);

        mCancel.setTextColor(sNotzColor.getAppAccentColor(this));
        mTabLayout.setSelectedTabIndicatorColor(sNotzColor.getAppAccentColor(this));
        mTabLayout.setTabTextColors(sCommonUtils.getColor(R.color.color_white, this), sNotzColor.getAppAccentColor(this));

        sPagerAdapter adapter = new sPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new WidgetNotesFragment(), getString(R.string.select_note));
        adapter.AddFragment(new WidgetChecklistsFragment(), getString(R.string.select_checklist));

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
                mViewPager.setAdapter(adapter);
                mTabLayout.setupWithViewPager(mViewPager);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                sCommonUtils.toast(getString(R.string.authentication_failed), WidgetActivity.this).show();
            }
        });

        Utils.showBiometricPrompt(this);

        if (sCommonUtils.getBoolean("use_biometric", false, this)) {
            mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
        } else if (Security.isPINEnabled(this)) {
            new AuthenticatorInterface(false, getString(R.string.authenticate), this) {

                @Override
                public void positiveButtonLister(Editable authText) {
                    if (authText != null && authText.toString().trim().length() == 4
                            && authText.toString().trim().equals(Security.getPIN(WidgetActivity.this))) {
                        mViewPager.setAdapter(adapter);
                        mTabLayout.setupWithViewPager(mViewPager);
                    }
                }
            }.show();
        } else {
            mViewPager.setAdapter(adapter);
            mTabLayout.setupWithViewPager(mViewPager);
        }

        mCancel.setOnClickListener(v -> finish());
    }

}