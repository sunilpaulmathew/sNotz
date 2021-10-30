package com.sunilpaulmathew.snotz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.sunilpaulmathew.snotz.activities.WelcomeActivity;
import com.sunilpaulmathew.snotz.fragments.sNotzFragment;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzWidgets;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);

        if (!Utils.getBoolean("welcome_message", false, this)) {
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
            Utils.saveBoolean("welcome_message", true, this);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                getMainFragment()).commit();

    }

    private Fragment getMainFragment() {
        int extraNoteId = getIntent().getIntExtra(sNotzWidgets.getNoteID(), sNotzWidgets.getInvalidNoteId());
        String extraCheckListPath = getIntent().getStringExtra(sNotzWidgets.getChecklistPath());

        Bundle bundle = new Bundle();
        if (extraCheckListPath != null) {
            bundle.putString(sNotzWidgets.getChecklistPath(), extraCheckListPath);
        } else {
            bundle.putInt(sNotzWidgets.getNoteID(), extraNoteId);
        }

        Fragment sNotzFragment = new sNotzFragment();
        sNotzFragment.setArguments(bundle);
        return sNotzFragment;
    }

}