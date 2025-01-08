package com.sunilpaulmathew.snotz.utils.dialogs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on March 18, 2023
 */
public class SaveAsTextDialog {

    public SaveAsTextDialog(@NonNull String note, Context context) {
        new EditTextInterface(null, null, (Activity) context) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    String fileName = s.toString().trim();
                    if (!fileName.endsWith(".txt")) {
                        fileName += ".txt";
                    }
                    if (fileName.contains(" ")) {
                        fileName = fileName.replace(" ", "_");
                    }
                    if (Build.VERSION.SDK_INT >= 29) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                            OutputStream outputStream = context.getContentResolver().openOutputStream(Objects.requireNonNull(uri));
                            Objects.requireNonNull(outputStream).write(note.getBytes());
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    } else {
                        sFileUtils.create(note, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName));
                    }
                    sCommonUtils.toast(context.getString(R.string.save_text_message,
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName), context).show();
                } else {
                    sCommonUtils.toast(context.getString(R.string.text_empty), context).show();
                }
            }
        }.show();
    }

}