package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class sNotzWidgets {

    private static final String mNOTE_ID = "noteId", mCHECKLIST_PATH = "checklistPath";

    public static int getInvalidNoteId() {
        return Integer.MIN_VALUE;
    }

    public static String getChecklistPath() {
        return mCHECKLIST_PATH;
    }

    public static String getChecklistPath(int appWidgetId, Context context) {
        return Utils.getString("appwidget" + appWidgetId, null, context);
    }

    public static String getNoteID() {
        return mNOTE_ID;
    }

    public static int getNoteID(int appWidgetId, Context context) {
        try {
            return Integer.parseInt(getChecklistPath(appWidgetId, context));
        } catch (Exception ignored) {}
        return getInvalidNoteId();
    }

    public static String getWidgetText(String path) {
        if (!CheckLists.isValidCheckList(Utils.read(path))) {
            return null;
        }
        String jsonString = Utils.read(path);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(CheckLists.getChecklists(jsonString)).size(); i++) {
            JsonObject object = Objects.requireNonNull(CheckLists.getChecklists(jsonString)).get(i).getAsJsonObject();
            sb.append(CheckLists.isDone(object) ? "\u2611  " : "\u2610  ").append(CheckLists.getTitle(object)).append("\n");
        }
        return sb.toString();
    }

}