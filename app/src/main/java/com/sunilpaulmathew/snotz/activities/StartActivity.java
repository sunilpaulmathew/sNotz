package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.MainActivity;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;
import com.sunilpaulmathew.snotz.utils.AsyncTasks;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzData;
import com.sunilpaulmathew.snotz.utils.sNotzItems;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

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

        // Update widgets on app launch
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(
                getApplication(), WidgetProvider.class));
        WidgetProvider mWidgetProvider = new WidgetProvider();
        mWidgetProvider.onUpdate(this, AppWidgetManager.getInstance(this), ids);

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
                startMainActivity();
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

        if (!Utils.getBoolean("reOrganized", false, this)) {
            reOrganizeNotes(this).execute();
        } else {
            if (Utils.getBoolean("use_biometric", false, this)) {
                mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
            } else if (Security.isPINEnabled(this)) {
                Security.authenticate(false, null, this);
            } else {
                startMainActivity();
            }
        }
    }

    private AsyncTasks reOrganizeNotes(Activity activity) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void doInBackground() {
                int i = 0;
                JsonObject mJSONObject = new JsonObject();
                JsonArray mJSONArray = new JsonArray();
                for (sNotzItems items : sNotzData.getRawData(activity)) {
                    JsonObject note = new JsonObject();
                    note.addProperty("note", items.getNote());
                    note.addProperty("date", items.getTimeStamp());
                    note.addProperty("image", items.getImageString());
                    note.addProperty("hidden", items.isHidden());
                    note.addProperty("colorBackground", items.getColorBackground());
                    note.addProperty("colorText", items.getColorText());
                    note.addProperty("noteID", i);
                    i++;
                    mJSONArray.add(note);
                }
                mJSONObject.add("sNotz", mJSONArray);
                Utils.create(mJSONObject.toString(), activity.getFilesDir().getPath() + "/snotz");
            }

            @Override
            public void onPostExecute() {
                Utils.saveBoolean("reOrganized", true, activity);
                if (Utils.getBoolean("use_biometric", false, activity)) {
                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(activity));
                } else if (Security.isPINEnabled(activity)) {
                    Security.authenticate(false, null, activity);
                } else {
                    startMainActivity();
                }
            }
        };
    }

    private void startMainActivity() {
        int extraNoteId = getIntent().getIntExtra(sNotzWidgets.getNoteID(), sNotzWidgets.getInvalidNoteId());
        String extraCheckListPath = getIntent().getStringExtra(sNotzWidgets.getChecklistPath());
        Intent mainActivity = new Intent(this, MainActivity.class);
        if (extraCheckListPath != null) {
            mainActivity.putExtra(sNotzWidgets.getChecklistPath(), extraCheckListPath);
        } else if (extraNoteId != sNotzWidgets.getInvalidNoteId()) {
            mainActivity.putExtra(sNotzWidgets.getNoteID(), extraNoteId);
        }
        startActivity(mainActivity);
        finish();
    }

}