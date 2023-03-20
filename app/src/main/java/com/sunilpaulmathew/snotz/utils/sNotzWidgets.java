package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.providers.WidgetProvider;

import java.io.File;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class sNotzWidgets {

    private static boolean mWidget = false;
    private static final String mNOTE_ID = "noteId", mCHECKLIST_PATH = "checklistPath";

    public static boolean isWidget() {
        return mWidget;
    }

    public static int getInvalidNoteId() {
        return Integer.MIN_VALUE;
    }

    public static String getChecklistPath() {
        return mCHECKLIST_PATH;
    }

    public static String getChecklistPath(int appWidgetId, Context context) {
        return sCommonUtils.getString("appwidget" + appWidgetId, null, context);
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
        if (!CheckLists.isValidCheckList(sFileUtils.read(new File(path)))) {
            return null;
        }
        String jsonString = sFileUtils.read(new File(path));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(CheckLists.getChecklists(jsonString)).size(); i++) {
            JsonObject object = Objects.requireNonNull(CheckLists.getChecklists(jsonString)).get(i).getAsJsonObject();
            sb.append(CheckLists.isDone(object) ? "\u2611  " : "\u2610  ").append(CheckLists.getTitle(object)).append("\n");
        }
        return sb.toString();
    }

    public static void create(int mAppWidgetId, Activity activity) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
        WidgetProvider.update(appWidgetManager, mAppWidgetId, activity);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        activity.setResult(Activity.RESULT_OK, resultValue);
        activity.finish();
    }

    public static void isWidget(boolean b) {
        mWidget = b;
    }

}