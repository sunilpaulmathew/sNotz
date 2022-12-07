package com.sunilpaulmathew.snotz.interfaces;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Security;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on December 04, 2022
 */
public abstract class AuthenticatorInterface {

    private final boolean mLogin;
    private final Activity mActivity;
    private final MaterialAlertDialogBuilder mDialogBuilder;
    private final String mTitle;

    public AuthenticatorInterface(boolean login, String title, Activity activity) {
        this.mLogin = login;
        this.mTitle = title;
        this.mActivity = activity;
        this.mDialogBuilder = new MaterialAlertDialogBuilder(activity);
    }

    private void startDialog() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(mActivity);
        View mEditLayout = mLayoutInflater.inflate(R.layout.layout_auth, null);
        AppCompatAutoCompleteTextView mText = mEditLayout.findViewById(R.id.text);

        mText.requestFocus();
        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.toString().trim().length() == 4 && Security.getPIN(mActivity) != null
                        && !s.toString().trim().equals(Security.getPIN(mActivity))) {
                    mText.setText(null);
                    sUtils.toast(mActivity.getString(R.string.pin_mismatch_message), mActivity).show();
                }
            }
        });

        mDialogBuilder.setTitle(mTitle);
        mDialogBuilder.setView(mEditLayout);
        mDialogBuilder.setIcon(R.mipmap.ic_launcher);
        if (mLogin) {
            mDialogBuilder.setCancelable(false);
        }
        mDialogBuilder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            if (mLogin) {
                mActivity.finish();
            }
        });
        mDialogBuilder.setPositiveButton(R.string.ok, (dialog, id) ->
                positiveButtonLister(mText.getText())
        ).show();
    }

    public void show() {
        startDialog();
    }

    public abstract void positiveButtonLister(Editable authText);

}