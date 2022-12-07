package com.sunilpaulmathew.snotz.interfaces;

import android.app.Activity;
import android.text.Editable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public abstract class EditTextInterface {

    private final Activity mActivity;
    private final MaterialAlertDialogBuilder mDialogBuilder;
    private final String mText, mTitle;

    public EditTextInterface(String text, String title, Activity activity) {
        this.mText = text;
        this.mTitle = title;
        this.mActivity = activity;
        this.mDialogBuilder = new MaterialAlertDialogBuilder(activity);
    }

    private void startDialog() {
        LinearLayout layout = new LinearLayout(mActivity);
        layout.setPadding(75, 75, 75, 75);
        final AppCompatEditText editText = new AppCompatEditText(mActivity);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (mText != null) {
            editText.append(mText);
        }
        editText.setSingleLine(true);
        editText.requestFocus();
        layout.addView(editText);

        if (mTitle != null) {
            mDialogBuilder.setTitle(mTitle);
            mDialogBuilder.setIcon(R.mipmap.ic_launcher);
        }
        mDialogBuilder.setView(layout);
        mDialogBuilder.setIcon(R.mipmap.ic_launcher);
        mDialogBuilder.setNegativeButton(R.string.cancel, (dialog, id) -> {
        });
        mDialogBuilder.setPositiveButton(R.string.ok, (dialog, id) ->
                positiveButtonLister(editText.getText())
        ).show();
    }

    public void show() {
        startDialog();
    }

    public abstract void positiveButtonLister(Editable s);

}