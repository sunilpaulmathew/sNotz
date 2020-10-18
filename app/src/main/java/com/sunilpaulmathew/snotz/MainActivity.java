package com.sunilpaulmathew.snotz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sunilpaulmathew.snotz.utils.AboutActivity;
import com.sunilpaulmathew.snotz.utils.CreateNoteActivity;
import com.sunilpaulmathew.snotz.utils.RecycleViewAdapter;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotz;
import com.sunilpaulmathew.snotz.utils.sNotzColor;

import java.io.File;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class MainActivity extends AppCompatActivity {

    private AppCompatImageButton mSearchButton;
    private AppCompatImageButton mSettingsButton;
    private AppCompatImageButton mInfoButton;
    private AppCompatTextView mAppTitle;
    private AppCompatEditText mSearchWord;
    private boolean mExit;
    private Handler mHandler = new Handler();
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize App Theme
        Utils.initializeAppTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppTitle = findViewById(R.id.app_title);
        mSearchButton = findViewById(R.id.search_button);
        mSettingsButton = findViewById(R.id.settings_button);
        mInfoButton = findViewById(R.id.info_button);
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
            mAppTitle.setVisibility(View.GONE);
            mSearchButton.setVisibility(View.GONE);
            mSettingsButton.setVisibility(View.GONE);
            mInfoButton.setVisibility(View.GONE);
            mSearchWord.setVisibility(View.VISIBLE);
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

        mSettingsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, mSettingsButton);
            Menu menu = popupMenu.getMenu();
            SubMenu color = menu.addSubMenu(Menu.NONE, 0, Menu.NONE, getString(R.string.note_color));
            color.add(Menu.NONE, 1, Menu.NONE, getString(R.string.note_color_background));
            color.add(Menu.NONE, 2, Menu.NONE, getString(R.string.note_color_text));
            if (Utils.existFile(getFilesDir().getPath() + "/snotz")) {
                menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.reverse_order)).setCheckable(true)
                        .setChecked(Utils.getBoolean("reverse_order", false, this));
                menu.add(Menu.NONE, 7, Menu.NONE, getString(R.string.show_hidden_notes)).setCheckable(true)
                        .setChecked(Utils.getBoolean("hidden_note", false, this));
                menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.backup_notes));
                menu.add(Menu.NONE, 5, Menu.NONE, getString(R.string.clear_notes));
            } else {
                menu.add(Menu.NONE, 6, Menu.NONE, getString(R.string.restore_notes));
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0:
                        break;
                    case 1:
                        Utils.mTextColor = false;
                        sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("note_background",
                                this)), "note_background", this);
                        break;
                    case 2:
                        Utils.mTextColor = true;
                        sNotzColor.colorDialog(sNotzColor.getColors(this).indexOf(sNotzColor.setAccentColor("text_color",
                                this)), "text_color", this);
                        break;

                    case 3:
                        if (Utils.getBoolean("reverse_order", false, this)) {
                            Utils.saveBoolean("reverse_order", false, this);
                        } else {
                            Utils.saveBoolean("reverse_order", true, this);
                        }
                        Utils.reloadUI(this);
                        break;
                    case 4:
                        Utils.create(Utils.readFile(getFilesDir().getPath() + "/snotz"), Environment.getExternalStorageDirectory().toString() + "/snotz.backup/");
                        Utils.showSnackbar(mAppTitle, getString(R.string.backup_notes_message, Environment.getExternalStorageDirectory().toString() + "/snotz.backup/"));
                        break;
                    case 5:
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.clear_notes_message))
                                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                })
                                .setPositiveButton(R.string.delete, (dialog, which) -> {
                                    Utils.deleteFile(getFilesDir().getPath() + "/snotz");
                                    Utils.reloadUI(this);
                                })
                                .show();
                        break;
                    case 6:
                        Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                        restore.setType("*/*");
                        startActivityForResult(restore, 0);
                        break;
                    case 7:
                        if (Utils.getBoolean("hidden_note", false, this)) {
                            Utils.saveBoolean("hidden_note", false, this);
                        } else {
                            Utils.saveBoolean("hidden_note", true, this);
                        }
                        Utils.reloadUI(this);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        mInfoButton.setOnClickListener(v -> {
            Intent aboutsNotz = new Intent(this, AboutActivity.class);
            startActivity(aboutsNotz);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File file = new File(Objects.requireNonNull(uri.getPath()));
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mPath = Environment.getExternalStorageDirectory().toString() + "/Download/" +
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                mPath = Utils.getPath(file);
            }
            if (!sNotz.validBackup(mPath)) {
                Utils.showSnackbar(mAppTitle, getString(R.string.restore_error));
                return;
            }
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.restore_notes_question, new File(mPath).getName()))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        Utils.create(Utils.readFile(mPath), getFilesDir().getPath() + "/snotz");
                        Utils.reloadUI(this);
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed () {
        if (mSearchWord.getVisibility() == View.VISIBLE) {
            if (Utils.mSearchText != null) {
                Utils.mSearchText = null;
                mSearchWord.setText(null);
            }
            mSearchWord.setVisibility(View.GONE);
            mAppTitle.setVisibility(View.VISIBLE);
            mSearchButton.setVisibility(View.VISIBLE);
            mSettingsButton.setVisibility(View.VISIBLE);
            mInfoButton.setVisibility(View.VISIBLE);
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