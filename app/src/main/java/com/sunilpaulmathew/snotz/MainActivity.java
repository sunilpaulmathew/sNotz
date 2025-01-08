package com.sunilpaulmathew.snotz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.sunilpaulmathew.snotz.activities.WelcomeActivity;
import com.sunilpaulmathew.snotz.fragments.sNotzFragment;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CrashReporter.sCrashReporter;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

        // Record crashes
        sCrashReporter crashReporter = new sCrashReporter(this);
        crashReporter.setAccentColor(Integer.MIN_VALUE);
        crashReporter.setTitleSize(25);
        crashReporter.initialize();

        if (!sCommonUtils.getBoolean("welcome_message", false, this)) {
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
            sCommonUtils.saveBoolean("welcome_message", true, this);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                getMainFragment()).commit();

    }

    private Fragment getMainFragment() {
        int extraNoteId = getIntent().getIntExtra("noteId", Integer.MIN_VALUE);
        String externalNote = getIntent().getStringExtra("externalNote");

        Bundle bundle = new Bundle();
        if (extraNoteId != Integer.MIN_VALUE) {
            bundle.putInt("noteId", extraNoteId);
        } else if (externalNote != null) {
            bundle.putString("externalNote", externalNote);
        }

        Fragment sNotzFragment = new sNotzFragment();
        sNotzFragment.setArguments(bundle);
        return sNotzFragment;
    }

}