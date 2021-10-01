package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on June 06, 2021
 */
public class RestoreNotesActivity extends AppCompatActivity {

    private AppCompatEditText mText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_notes);

        mText = findViewById(R.id.text);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);
        MaterialTextView mInsert = findViewById(R.id.insert_button);

        mText.setTextColor(Utils.isDarkTheme(this) ? getResources().getColor(R.color.color_white) : getResources()
                .getColor(R.color.color_black));

        mInsert.setOnClickListener(v -> {
            if (mText.getText() == null || mText.getText().toString().isEmpty()) {
                return;
            }
            if (sNotzUtils.validBackup(mText.getText().toString())) {
                sNotzUtils.restoreNotes(mText.getText().toString(), this);
                Utils.reloadUI(this);
                finish();
            } else {
                Utils.showSnackbar(findViewById(android.R.id.content), getString(R.string.restore_data_invalid));
            }
        });

        mCancel.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (mText.getText() != null && !mText.getText().toString().isEmpty()) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.discard_note))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.discard), (dialogInterface, i) -> finish()).show();
        } else {
            finish();
        }
    }

}