/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        MaterialTextView mAppName = findViewById(R.id.app_title);
        AppCompatImageView mSunil = findViewById(R.id.sunil);
        MaterialTextView mChange_logs = findViewById(R.id.change_logs);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        LinearLayout mSourceCode = findViewById(R.id.source_code);
        LinearLayout mMoreApps = findViewById(R.id.play_store);
        LinearLayout mReportIssue = findViewById(R.id.report_issue);

        mSourceCode.setOnClickListener(v -> Utils.launchURL(mSourceCode, "https://github.com/sunilpaulmathew/sNotz/", this));
        mMoreApps.setOnClickListener(v -> Utils.launchURL(mMoreApps, "https://play.google.com/store/apps/dev?id=5836199813143882901", this));
        mReportIssue.setOnClickListener(v -> Utils.launchURL(mReportIssue, "https://github.com/sunilpaulmathew/sNotz/issues/new", this));
        mSunil.setOnClickListener(v -> Utils.launchURL(mSunil, "https://github.com/sunilpaulmathew", this));
        mCancel.setOnClickListener(v -> onBackPressed());

        mAppName.setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        String change_log = null;
        try {
            change_log = new JSONObject(Objects.requireNonNull(Utils.readAssetFile(
                    this, "changelogs.json"))).getString("releaseNotes");
        } catch (JSONException ignored) {
        }
        mChange_logs.setText(change_log);
    }

}