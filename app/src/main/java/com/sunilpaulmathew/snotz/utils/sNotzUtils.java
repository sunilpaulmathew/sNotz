package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.text.Editable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzUtils {

    private static JSONObject mJSONObject;
    private static JSONArray mJSONArray;
    private static int i;

    private static List<sNotzItems> getNotesFromBackup(String backupData, Context context) {
        List<sNotzItems> mRestoreData = new ArrayList<>();
        for (int i = 0; i < sNotzData.getsNotzItems(backupData).length(); i++) {
            try {
                JSONObject command = Objects.requireNonNull(sNotzData.getsNotzItems(backupData)).getJSONObject(i);
                mRestoreData.add(new sNotzItems(sNotzData.getNote(command.toString()), sNotzData.getDate(command.toString()), sNotzData.isHidden(command.toString()),
                        sNotzData.getBackgroundColor(command.toString(), context), sNotzData.getTextColor(command.toString(), context), i));
            } catch (JSONException ignored) {
            }
        }
        return mRestoreData;
    }

    public static String sNotzToText(Context context) {
        StringBuilder sb = new StringBuilder();
        for (int note = 0; note < Objects.requireNonNull(sNotzData.getsNotzItems(Utils.read(context.getFilesDir().getPath() + "/snotz"))).length(); note++) {
            try {
                JSONObject command = Objects.requireNonNull(sNotzData.getsNotzItems(Utils.read(context.getFilesDir().getPath() + "/snotz"))).getJSONObject(note);
                sb.append(sNotzData.getNote(command.toString())).append("\n... ... ... ... ...\n\n");
            } catch (JSONException ignored) {
            }
        }
        return sb.toString();
    }

    public static boolean validBackup(String backupData) {
        return sNotzData.getsNotzItems(backupData) != null;
    }

    public static void addNote(Editable newNote, int colorBg, int colorTxt, boolean hidden,
                               Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (sNotzItems items : sNotzData.getRawData(context)) {
                JSONObject note = new JSONObject();
                note.put("note", items.getNote());
                note.put("date", items.getTimeStamp());
                note.put("hidden", items.isHidden());
                note.put("colorBackground", items.getColorBackground());
                note.put("colorText", items.getColorText());
                note.put("noteID", items.getNoteID());
                mJSONArray.put(note);
            }
            JSONObject note = new JSONObject();
            note.put("note", newNote);
            note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
            note.put("hidden", hidden);
            note.put("colorBackground", colorBg);
            note.put("colorText", colorTxt);
            note.put("noteID", sNotzData.getData(context).size());
            mJSONArray.put(note);
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

    public static void deleteNote(int noteID, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        i = 0;
        try {
            for (sNotzItems items : sNotzData.getRawData(context)) {
                if (items.getNoteID() != noteID) {
                    JSONObject note = new JSONObject();
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", items.isHidden());
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", i);
                    i++;
                    mJSONArray.put(note);
                    mJSONObject.put("sNotz", mJSONArray);
                    Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
                }
            }
        } catch (JSONException ignored) {
        }
    }

    public static void hideNote(int noteID, boolean hidden, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            for (sNotzItems items : sNotzData.getRawData(context)) {
                if (items.getNoteID() != noteID) {
                    JSONObject note = new JSONObject();
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", items.isHidden());
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", items.getNoteID());
                    mJSONArray.put(note);
                } else {
                    JSONObject note = new JSONObject();
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", hidden);
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", items.getNoteID());
                    mJSONArray.put(note);
                }
            }
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

    public static void initializeNotes(Editable newNote, int colorBg, int colorTxt, boolean hidden,
                                       Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        try {
            JSONObject note = new JSONObject();
            note.put("note", newNote);
            note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
            note.put("hidden", hidden);
            note.put("colorBackground", colorBg);
            note.put("colorText", colorTxt);
            note.put("noteID", 0);
            mJSONArray.put(note);
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

    public static void restoreNotes(String backupData, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        i = 0;
        try {
            if (Utils.exist(context.getFilesDir().getPath() + "/snotz")) {
                for (sNotzItems items : sNotzData.getRawData(context)) {
                    JSONObject note = new JSONObject();
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", items.isHidden());
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", i);
                    i++;
                    mJSONArray.put(note);
                }
            }
            if (validBackup(backupData)) {
                for (sNotzItems items : getNotesFromBackup(backupData, context)) {
                    JSONObject note = new JSONObject();
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", items.isHidden());
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", i);
                    i++;
                    mJSONArray.put(note);
                }
            }
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

    public static void updateNote(Editable newNote, String oldNote, int colorBg, int colorTxt,
                                  boolean hidden, Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        i = 0;
        try {
            for (sNotzItems items : sNotzData.getRawData(context)) {
                JSONObject note = new JSONObject();
                if (items.getNote().equals(oldNote)) {
                    note.put("note", newNote);
                    note.put("date", DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
                    note.put("hidden", hidden);
                    note.put("colorBackground", colorBg);
                    note.put("colorText", colorTxt);
                    note.put("noteID", i);
                    i++;
                } else {
                    note.put("note", items.getNote());
                    note.put("date", items.getTimeStamp());
                    note.put("hidden", items.isHidden());
                    note.put("colorBackground", items.getColorBackground());
                    note.put("colorText", items.getColorText());
                    note.put("noteID", i);
                }
                mJSONArray.put(note);
            }
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

    public static void reOrganizeNotes(Context context) {
        mJSONObject = new JSONObject();
        mJSONArray = new JSONArray();
        i = 0;
        try {
            for (sNotzItems items : sNotzData.getRawData(context)) {
                JSONObject note = new JSONObject();
                note.put("note", items.getNote());
                note.put("date", items.getTimeStamp());
                note.put("hidden", items.isHidden());
                note.put("colorBackground", items.getColorBackground());
                note.put("colorText", items.getColorText());
                note.put("noteID", i);
                i++;
                mJSONArray.put(note);
            }
            mJSONObject.put("sNotz", mJSONArray);
            Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
        } catch (JSONException ignored) {
        }
    }

}