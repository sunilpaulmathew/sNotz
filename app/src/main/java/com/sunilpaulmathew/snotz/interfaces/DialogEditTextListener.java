package com.sunilpaulmathew.snotz.interfaces;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 13, 2020
 */
public interface DialogEditTextListener {

    static MaterialAlertDialogBuilder dialogEditText(String text, String title, final DialogInterface.OnClickListener negativeListener,
                                                            final DialogEditTextListener onDialogEditTextListener, int inputType,
                                                            Activity activity) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setPadding(75, 75, 75, 75);

        final AppCompatEditText editText = new AppCompatEditText(activity);
        editText.setGravity(Gravity.CENTER);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (text != null) {
            editText.append(text);
        }
        editText.setSingleLine(true);
        editText.requestFocus();
        if (inputType >= 0) {
            editText.setInputType(inputType);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 4) {
                        Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.pin_length_warning));
                        editText.setTextColor(sNotzUtils.getColor(R.color.color_red, activity));
                    } else {
                        editText.setTextColor(sNotzUtils.getColor(Utils.isDarkTheme(activity) ? R.color.color_white : R.color.color_black, activity));
                    }
                }
            });
        }

        layout.addView(editText);

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity).setView(layout);
        if (title != null) {
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setTitle(title);
        }
        if (negativeListener != null) {
            dialog.setNegativeButton(activity.getString(R.string.cancel), negativeListener);
        }
        if (onDialogEditTextListener != null) {
            dialog.setPositiveButton(activity.getString(R.string.ok), (dialog1, which)
                    -> onDialogEditTextListener.onClick(Objects.requireNonNull(editText.getText()).toString()))
                    .setOnDismissListener(dialog1 -> {
                        if (negativeListener != null) {
                            negativeListener.onClick(dialog1, 0);
                        }
                    });
        }
        return dialog;
    }

    void onClick(String toString);

}