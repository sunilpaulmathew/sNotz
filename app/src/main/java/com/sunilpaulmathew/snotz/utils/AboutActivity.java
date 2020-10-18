/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of The Translator, An application to help translate android apps.
 *
 */

package com.sunilpaulmathew.snotz.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

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

        AppCompatTextView mAppName = findViewById(R.id.app_title);
        AppCompatImageView mSunil = findViewById(R.id.sunil);
        AppCompatTextView mChange_logs = findViewById(R.id.change_logs);
        AppCompatTextView mCancel = findViewById(R.id.cancel_button);
        mSunil.setOnClickListener(v -> {
            Utils.launchURL(mSunil, "https://github.com/sunilpaulmathew", this);
        });
        mCancel.setOnClickListener(v -> {
            onBackPressed();
        });

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