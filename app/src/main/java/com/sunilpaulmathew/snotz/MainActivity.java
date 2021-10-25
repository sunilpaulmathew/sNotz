package com.sunilpaulmathew.snotz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sunilpaulmathew.snotz.activities.WelcomeActivity;
import com.sunilpaulmathew.snotz.fragments.sNotzFragment;
import com.sunilpaulmathew.snotz.utils.Consts;
import com.sunilpaulmathew.snotz.utils.Utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        int extraNoteId = getIntent().getIntExtra(Consts.EXTRAS.NOTE_ID, Consts.INVALID_NOTE_ID);

        Bundle bundle = new Bundle();
        bundle.putInt(Consts.EXTRAS.NOTE_ID, extraNoteId);

        Fragment sNotzFragment = new sNotzFragment();
        sNotzFragment.setArguments(bundle);
        return sNotzFragment;
    }

}