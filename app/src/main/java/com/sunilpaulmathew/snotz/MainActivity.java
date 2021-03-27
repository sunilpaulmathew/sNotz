package com.sunilpaulmathew.snotz;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.utils.AboutActivity;
import com.sunilpaulmathew.snotz.utils.CreateNoteActivity;
import com.sunilpaulmathew.snotz.utils.RecycleViewAdapter;
import com.sunilpaulmathew.snotz.utils.SettingsActivity;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.WelcomeActivity;
import com.sunilpaulmathew.snotz.utils.sNotz;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mMenu, mSearchButton, mSortButton;
    private MaterialTextView mAppTitle;
    private AppCompatEditText mSearchWord;
    private boolean mExit;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppTitle = findViewById(R.id.app_title);
        mSearchButton = findViewById(R.id.search_button);
        mSortButton = findViewById(R.id.sort_button);
        mMenu = findViewById(R.id.settings_button);
        mSearchWord = findViewById(R.id.search_word);
        mSearchWord.setTextColor(Color.RED);
        FloatingActionButton mFAB = findViewById(R.id.fab);
        Utils.mRecyclerView = findViewById(R.id.recycler_view);

        Utils.mRecyclerView.setLayoutManager(new GridLayoutManager(this, Utils.getSpanCount(this)));
        Utils.mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Utils.mRecyclerView.setAdapter(new RecycleViewAdapter(sNotz.getData(this)));

        mFAB.setOnClickListener(v -> {
            if (Utils.isPermissionDenied(this)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                Utils.mName = null;
                Intent createNote = new Intent(this, CreateNoteActivity.class);
                startActivity(createNote);
            }
        });
        mSearchButton.setOnClickListener(v -> {
            mSearchButton.setVisibility(View.GONE);
            mSortButton.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            mSearchWord.setVisibility(View.VISIBLE);
            Utils.toggleKeyboard(mSearchWord, this);
        });

        mSearchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                Utils.mSearchText = s.toString().toLowerCase();
                Utils.reloadUI(MainActivity.this);
            }
        });

        mSortButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mSortButton);
            Menu menu = popupMenu.getMenu();
            if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.created_order)).setCheckable(true)
                        .setChecked(Utils.getBoolean("date_created", true, this));
                menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.az_order)).setCheckable(true)
                        .setChecked(Utils.getBoolean("az_order", false, this));
                menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.reverse_order)).setCheckable(true)
                        .setChecked(Utils.getBoolean("reverse_order", false, this));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        if (Utils.getBoolean("date_created", true, this)) {
                            Utils.saveBoolean("date_created", false, this);
                        } else {
                            Utils.saveBoolean("date_created", true, this);
                            Utils.saveBoolean("az_order", false, this);
                        }
                        Utils.reloadUI(this);
                        break;
                    case 1:
                        if (Utils.getBoolean("az_order", false, this)) {
                            Utils.saveBoolean("az_order", false, this);
                        } else {
                            Utils.saveBoolean("az_order", true, this);
                            Utils.saveBoolean("date_created", false, this);
                        }
                        Utils.reloadUI(this);
                        break;
                    case 2:
                        if (Utils.getBoolean("reverse_order", false, this)) {
                            Utils.saveBoolean("reverse_order", false, this);
                        } else {
                            Utils.saveBoolean("reverse_order", true, this);
                        }
                        Utils.reloadUI(this);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mMenu);
            Menu menu = popupMenu.getMenu();
            menu.add(Menu.NONE, 0, Menu.NONE, getString(R.string.settings));
            menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.about));
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        Intent settings = new Intent(this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                    case 1:
                        Intent aboutsNotz = new Intent(this, AboutActivity.class);
                        startActivity(aboutsNotz);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!Utils.getBoolean("welcome_message", false, this)) {
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
            Utils.saveBoolean("welcome_message", true, this);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchWord.getVisibility() == View.VISIBLE) {
            if (Utils.mSearchText != null) {
                Utils.mSearchText = null;
                mSearchWord.setText(null);
            }
            mSearchWord.setVisibility(View.GONE);
            mSearchButton.setVisibility(View.VISIBLE);
            mSortButton.setVisibility(View.VISIBLE);
            mMenu.setVisibility(View.VISIBLE);
            return;
        }
        if (mExit) {
            mExit = false;
            super.onBackPressed();
        } else {
            Utils.showSnackbar(mAppTitle, getString(R.string.press_back_exit));
            mExit = true;
            mHandler.postDelayed(() -> mExit = false, 2000);
        }
    }

}