package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.utils.serializableItems.CheckListItems;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 10, 2021
 */
public class CheckLists {

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
            if (item.getTitle() != null && !item.getTitle().trim().isEmpty()) {
                JsonObject checklist = new JsonObject();
                checklist.addProperty("title", item.getTitle());
                checklist.addProperty("done", item.isChecked());
                jsonArray.add(checklist);
            }
        }
        return jsonArray;
    }

    public static List<File> getOldChecklists(Context context) {
        List<File> mData = new ArrayList<>();
        for (File checklists : Objects.requireNonNull(Objects.requireNonNull(context.getExternalFilesDir("checklists")).listFiles())) {
            if (CheckLists.isValidCheckList(sFileUtils.read(checklists))) {
                mData.add(checklists);
            }
        }
        return mData;
    }

    public static String getChecklistString(List<CheckListItems> items) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("checklist", getChecklists(items));
        return jsonObject.toString();
    }

}