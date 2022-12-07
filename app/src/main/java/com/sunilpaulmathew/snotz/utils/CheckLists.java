package com.sunilpaulmathew.snotz.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.CheckListsActivity;
import com.sunilpaulmathew.snotz.interfaces.EditTextInterface;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckLists implements Serializable {

    private static String mCheckListName;

    static boolean isDone(JsonObject object) {
        return object.get("done").getAsBoolean();
    }

    public static boolean isValidCheckList(String checkListString) {
        return getChecklists(checkListString) != null;
    }

    public static JsonArray getChecklists(String checkListString) {
        try {
            return Objects.requireNonNull(sNotzData.getJSONObject(checkListString)).getAsJsonArray("checklist");
        } catch (Exception ignored) {}
        return null;
    }

    public static JsonArray getChecklists(List<CheckListItems> items) {
        JsonArray jsonArray = new JsonArray();
        for (CheckListItems item : items) {
            if (!item.getTitle().equals("")) {
                JsonObject checklist = new JsonObject();
                checklist.addProperty("title", item.getTitle());
                checklist.addProperty("done", item.isChecked());
                jsonArray.add(checklist);
            }
        }
        return jsonArray;
    }

    public static List<CheckListItems> getData(Context context) {
        List<CheckListItems> mSavedData = new ArrayList<>();
        String jsonString = sUtils.read(new File(context.getExternalFilesDir("checklists"), getCheckListName()));
        for (int i = 0; i < Objects.requireNonNull(getChecklists(jsonString)).size(); i++) {
            JsonObject object = Objects.requireNonNull(getChecklists(jsonString)).get(i).getAsJsonObject();
            mSavedData.add(new CheckListItems(getTitle(object), isDone(object)));
        }
        return mSavedData;
    }

    public static List<File> getCheckLists(Context context) {
        List<File> mCheckLists = new ArrayList<>();
        for (File checklists : Objects.requireNonNull(context.getExternalFilesDir("checklists").listFiles())) {
            if (CheckLists.isValidCheckList(sUtils.read(checklists))) {
                mCheckLists.add(checklists);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(mCheckLists, Comparator.comparingLong(File::lastModified));
        }
        Collections.reverse(mCheckLists);
        return mCheckLists;
    }

    public static String getCheckListName() {
        return mCheckListName;
    }

    static String getTitle(JsonObject object) {
        return object.get("title").getAsString();
    }

    public static void backupCheckList(Activity activity) {
        if (Build.VERSION.SDK_INT < 30 && sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity)) {
            sPermissionUtils.requestPermission(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },activity);
            return;
        }
        new EditTextInterface(CheckLists.getCheckListName(), activity.getString(R.string.check_list_backup_question, CheckLists.getCheckListName()), activity) {

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
                            Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                            OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
                            outputStream.write(Objects.requireNonNull(sUtils.read(new File(activity.getExternalFilesDir("checklists"), CheckLists.getCheckListName()))).getBytes());
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    } else {
                        sUtils.create(sUtils.read(new File(activity.getExternalFilesDir("checklists"), CheckLists.getCheckListName())),
                                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName));
                    }
                    sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_checklist_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + fileName)).show();
                } else {
                    sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.text_empty)).show();
                }
            }
        }.show();
    }

    public static void importCheckList(String jsonString, boolean scan, Activity activity) {
        new EditTextInterface(null, activity.getString(R.string.check_list_import_question), activity) {

            @Override
            public void positiveButtonLister(Editable s) {
                if (s != null && !s.toString().trim().isEmpty()) {
                    if (sUtils.exist(new File(activity.getExternalFilesDir("checklists"), s.toString().trim()))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.check_list_exist_warning))
                                .setNegativeButton(activity.getString(R.string.change_name), (dialogInterface, i) -> importCheckList(jsonString, scan, activity))
                                .setPositiveButton(activity.getString(R.string.replace), (dialogInterface, i) -> {
                                    sUtils.create(jsonString, new File(activity.getExternalFilesDir("checklists"), s.toString().trim()));
                                    Intent checkListIntent = new Intent(activity, CheckListsActivity.class);
                                    activity.startActivity(checkListIntent);
                                    if (scan) {
                                        activity.finish();
                                    }
                                }).show();
                        return;
                    }
                    sUtils.create(jsonString, new File(activity.getExternalFilesDir("checklists"), s.toString().trim()));
                    Intent checkListIntent = new Intent(activity, CheckListsActivity.class);
                    activity.startActivity(checkListIntent);
                    if (scan) {
                        activity.finish();
                    }
                } else {
                    sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.check_list_name_empty_message)).show();
                }
            }
        }.show();
    }

    public static void setCheckListName(String name) {
        mCheckListName = name;
    }

}