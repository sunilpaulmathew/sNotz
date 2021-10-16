package com.sunilpaulmathew.snotz.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckLists implements Serializable {

    private static String mCheckListName;

    private static boolean isDone(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getBoolean("done");
        } catch (JSONException ignored) {
        }
        return false;
    }

    public static boolean isValidCheckList(String checkListString) {
        return getChecklists(checkListString) != null;
    }

    public static JSONArray getChecklists(String checkListString) {
        try {
            JSONObject main = new JSONObject(Objects.requireNonNull(checkListString));
            return main.getJSONArray("checklist");
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static List<CheckListItems> getData(Context context) {
        List<CheckListItems> mSavedData = new ArrayList<>();
        for (int i = 0; i < Objects.requireNonNull(getChecklists(Utils.read(context.getExternalFilesDir("checklists") + "/" + getCheckListName()))).length(); i++) {
            try {
                JSONObject command = Objects.requireNonNull(getChecklists(Utils.read(context.getExternalFilesDir("checklists") + "/" + getCheckListName()))).getJSONObject(i);
                mSavedData.add(new CheckListItems(getTitle(command.toString()), isDone(command.toString())));
            } catch (JSONException ignored) {
            }
        }
        return mSavedData;
    }

    public static String getCheckListName() {
        return mCheckListName;
    }

    private static String getTitle(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getString("title");
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static void backupCheckList(Activity activity) {
        if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(activity)) {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        DialogEditTextListener.dialogEditText(null, activity.getString(R.string.check_list_backup_question, CheckLists.getCheckListName()),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.text_empty));
                        return;
                    }
                    if (!text.endsWith(".checklist")) {
                        text += ".checklist";
                    }
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                            Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                            OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
                            outputStream.write(Objects.requireNonNull(Utils.read(activity.getExternalFilesDir("checklists") + "/" + CheckLists.getCheckListName())).getBytes());
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    } else {
                        Utils.create(Utils.read(activity.getExternalFilesDir("checklists") + "/" + CheckLists.getCheckListName()), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text);
                    }
                    Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_notes_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text));
                }, -1, activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    public static void setCheckListName(String name) {
        mCheckListName = name;
    }

}