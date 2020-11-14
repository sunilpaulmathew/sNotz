package com.sunilpaulmathew.snotz.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.sunilpaulmathew.snotz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */

public class CreateNoteActivity extends AppCompatActivity {

    private NestedScrollView mScrollView;
    private String mJSONNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);
        AppCompatImageButton mBack = findViewById(R.id.back_button);
        mBack.setOnClickListener(v -> onBackPressed());
        AppCompatImageButton mSave = findViewById(R.id.save_button);
        AppCompatEditText mContents = findViewById(R.id.contents);
        mScrollView = findViewById(R.id.scroll_view);
        Snackbar snackBar = Snackbar.make(mScrollView, getString(R.string.note_invalid_warning), Snackbar.LENGTH_INDEFINITE);
        mScrollView.setBackgroundColor(sNotzColor.setAccentColor("note_background", this));
        mContents.setTextColor(sNotzColor.setAccentColor("text_color", this));
        mContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (sNotz.isNoteInvalid(s.toString())) {
                    mContents.setTextColor(Color.RED);
                    snackBar.setAction(R.string.dismiss, v -> snackBar.dismiss());
                    snackBar.show();
                } else {
                    snackBar.dismiss();
                    mContents.setTextColor(sNotzColor.setAccentColor("text_color", CreateNoteActivity.this));
                }
            }
        });
        mContents.setOnClickListener(v -> {
            mScrollView.setAlpha(1);
        });
        if (Utils.mName != null) {
            mContents.setText(sNotz.getNote(Utils.mName));
        }
        mBack.setOnClickListener(v -> onBackPressed());
        mSave.setOnClickListener(v -> {
            if (mContents.getText() == null || mContents.getText().toString().isEmpty()) {
                Utils.showSnackbar(mScrollView, getString(R.string.text_empty));
                return;
            }
            if (sNotz.isNoteInvalid(mContents.getText().toString())) {
                new MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.note_saving_error)
                        .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                        })
                        .show();
                return;
            }
            String mJSON = getFilesDir().getPath() + "/snotz";
            if (Utils.mName != null) {
                mJSONNew = Objects.requireNonNull(Utils.readFile(mJSON))
                        .replace(Utils.mName,"{\"note\":\"" + mContents.getText() +
                        "\",\"date\":\"" + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()) +
                        "\",\"hidden\":" + sNotz.isHidden(Utils.mName) + "}");
            } else if (Utils.existFile(mJSON)) {
                try {
                    JSONObject note = new JSONObject();
                    note.put("note", mContents.getText());
                    note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                    note.put("hidden", false);
                    String newNote = note.toString();
                    mJSONNew = Objects.requireNonNull(Utils.readFile(mJSON))
                                .replace("}]", "}," + newNote + "]");
                } catch (JSONException ignored) {
                }
            } else {
                try {
                    JSONObject obj = new JSONObject();
                    JSONArray sNotz = new JSONArray();
                    JSONObject note = new JSONObject();
                    note.put("note", mContents.getText());
                    note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                    note.put("hidden", false);
                    sNotz.put(note);
                    obj.put("sNotz", sNotz);
                    mJSONNew = obj.toString();
                } catch (JSONException ignored) {
                }
            }
            Utils.create(mJSONNew, mJSON);
            Utils.reloadUI(this);
            onBackPressed();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Utils.showSnackbar(mScrollView, getString(R.string.click_again_message));
    }

    @Override
    public void onBackPressed() {
        if (Utils.mName != null) Utils.mName = null;
        super.onBackPressed();
    }

}