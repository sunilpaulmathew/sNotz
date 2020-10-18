package com.sunilpaulmathew.snotz.utils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
                        } else if (!Utils.getBoolean(command.toString(), false, context)) {
                            mData.add(command.toString());
                        }
                    } else if (Objects.requireNonNull(getNote(command.toString())).toLowerCase().contains(Utils.mSearchText.toLowerCase())) {
                        if (Utils.getBoolean("hidden_note", false, context)) {
                            mData.add(command.toString());
                        } else if (!Utils.getBoolean(command.toString(), false, context)) {
                            mData.add(command.toString());
                        }
                    }
                } catch (JSONException ignored) {
                }
            }
            if (!Utils.getBoolean("reverse_order", false, context)) {
                Collections.reverse(mData);
            }
        }
        return mData;
    }

    public static boolean validBackup(String path) {
        return getsNotzItems(Utils.readFile(path)) != null;
    }

}