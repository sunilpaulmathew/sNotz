package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AppCompatImageView mSunil = findViewById(R.id.sunil);
        MaterialButton mCancelButton = findViewById(R.id.cancel_button);
        MaterialButton mSourceCode = findViewById(R.id.source_code);
        MaterialButton mMoreApps = findViewById(R.id.play_store);
        MaterialButton mReportIssue = findViewById(R.id.report_issue);
        MaterialTextView mAppName = findViewById(R.id.app_title);
        MaterialTextView mMadeBy = findViewById(R.id.made_by);

        mAppName.setTextColor(sNotzColor.getAppAccentColor(this));
        mMadeBy.setTextColor(sNotzColor.getAppAccentColor(this));
        mSourceCode.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mMoreApps.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));
        mReportIssue.setIconTint(ColorStateList.valueOf(sNotzColor.getAppAccentColor(this)));

        mSourceCode.setOnClickListener(v -> sCommonUtils.launchUrl("https://github.com/sunilpaulmathew/sNotz/", this));
        mMoreApps.setOnClickListener(v -> sCommonUtils.launchUrl("https://play.google.com/store/apps/dev?id=5836199813143882901", this));
        mReportIssue.setOnClickListener(v -> sCommonUtils.launchUrl("https://github.com/sunilpaulmathew/sNotz/issues/new", this));
        mSunil.setOnClickListener(v -> sCommonUtils.launchUrl("https://github.com/sunilpaulmathew", this));
        mCancelButton.setOnClickListener(v -> finish());

        mAppName.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
    }

}