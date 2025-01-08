package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 21, 2021
 */
public class sNotzWidgets {

    public static int getNoteID(int appWidgetId, Context context) {
        try {
            return Integer.parseInt(sCommonUtils.getString("appwidget" + appWidgetId, null, context));
        } catch (Exception ignored) {}
        return Integer.MIN_VALUE;
    }

    public static String getWidgetText(String jsonString) {
        if (!CheckLists.isValidCheckList(jsonString)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(CheckLists.getChecklists(jsonString)).size(); i++) {
            JsonObject object = Objects.requireNonNull(CheckLists.getChecklists(jsonString)).get(i).getAsJsonObject();
            sb.append(object.get("done").getAsBoolean() ? "☑  " : "☐  ").append(object.get("title").getAsString()).append("\n");
        }
        return sb.toString().trim();
    }

}