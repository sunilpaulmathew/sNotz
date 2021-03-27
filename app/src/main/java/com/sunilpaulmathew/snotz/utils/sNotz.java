package com.sunilpaulmathew.snotz.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */

public class sNotz {

    public static List<String> mData = new ArrayList<>();

    public static JSONArray getsNotzItems(String json) {
        if (json != null && !json.isEmpty()) {
            try {
                JSONObject main = new JSONObject(json);
                return main.getJSONArray("sNotz");
            } catch (JSONException ignored) {
            }
        }
        return null;
    }

    public static String getNote(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getString("note");
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static String getDate(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getString("date");
        } catch (JSONException ignored) {
        }
        return null;
    }

    public static boolean isHidden(String string) {
        try {
            JSONObject obj = new JSONObject(string);
            return obj.getBoolean("hidden");
        } catch (JSONException ignored) {
        }
        return false;
    }

    public static boolean isNoteInvalid(String string) {
        try {
            new JSONObject("{\"note\":\"" + string +
                    "\",\"date\":\"" + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()) +
                    "\"}");
            return false;
        } catch (JSONException ex) {
            return true;
        }
    }

    public static List<String> getData(Context context) {
        mData.clear();
        String json = context.getFilesDir().getPath() + "/snotz";
        if (Utils.existFile(json)) {
            for (int i = 0; i < Objects.requireNonNull(getsNotzItems(Utils.readFile(json))).length(); i++) {
                try {
                    JSONObject command = Objects.requireNonNull(getsNotzItems(Utils.readFile(json))).getJSONObject(i);
                    if (Utils.mSearchText == null) {
                        if (Utils.getBoolean("hidden_note", false, context)) {
                            mData.add(command.toString());
                        } else if (!isHidden(command.toString())) {
                            mData.add(command.toString());
                        }
                    } else if (Objects.requireNonNull(getNote(command.toString())).toLowerCase().contains(Utils.mSearchText.toLowerCase())) {
                        if (Utils.getBoolean("hidden_note", false, context)) {
                            mData.add(command.toString());
                        } else if (!isHidden(command.toString())) {
                            mData.add(command.toString());
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
            if (Utils.getBoolean("az_order", false, context)) {
                Collections.sort(mData);
            }
            if (Utils.getBoolean("reverse_order", false, context)) {
                Collections.reverse(mData);
            }
        }
        return mData;
    }

    public static String getNotesFromBackup(String path) {
        List<String> mRestoreData = new ArrayList<>();
        if (Utils.existFile(path)) {
            for (int i = 0; i < Objects.requireNonNull(getsNotzItems(Utils.readFile(path))).length(); i++) {
                try {
                    JSONObject command = Objects.requireNonNull(getsNotzItems(Utils.readFile(path))).getJSONObject(i);
                    mRestoreData.add(command.toString());
                } catch (JSONException ignored) {
                }
            }
        }
        return mRestoreData.toString().substring(1, mRestoreData.toString().length()-1);
    }

    public static String sNotzToText(Context context) {
        StringBuilder sb = new StringBuilder();
        for (int note = 0; note < Objects.requireNonNull(getsNotzItems(Utils.readFile(context.getFilesDir().getPath() + "/snotz"))).length(); note++) {
            try {
                JSONObject command = Objects.requireNonNull(getsNotzItems(Utils.readFile(context.getFilesDir().getPath() + "/snotz"))).getJSONObject(note);
                sb.append(getNote(command.toString())).append("\n... ... ... ... ...\n\n");
            } catch (JSONException ignored) {
            }
        }
        return sb.toString();
    }

    public static boolean validBackup(String path) {
        return getsNotzItems(Utils.readFile(path)) != null;
    }

}