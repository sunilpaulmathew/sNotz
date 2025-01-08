package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzUtils {

    public static List<sNotzItems> getNotesFromBackup(String backupData, Context context) {
        List<sNotzItems> mRestoreData = new ArrayList<>();
        JsonArray sNotz = Objects.requireNonNull(sNotzData.getJSONObject(backupData)).getAsJsonArray("sNotz");
        for (int i = 0; i < sNotz.size(); i++) {
            mRestoreData.add(new sNotzItems(sNotzData.getNote(sNotz.get(i).getAsJsonObject()),
                    sNotzData.getDate(sNotz.get(i).getAsJsonObject()),
                    sNotzData.isHidden(sNotz.get(i).getAsJsonObject()),
                    sNotzData.getBackgroundColor(sNotz.get(i).getAsJsonObject(), context),
                    sNotzData.getTextColor(sNotz.get(i).getAsJsonObject(), context),
                    sNotzData.getNoteID(sNotz.get(i).getAsJsonObject()))
            );
        }
        return mRestoreData;
    }

    public static String sNotzToText(Context context) {
        StringBuilder sb = new StringBuilder();
        JsonArray sNotz = Objects.requireNonNull(sNotzData.getJSONObject(sFileUtils.read(new File(context.getFilesDir(), "snotz")))).getAsJsonArray("sNotz");
        for (int i = 0; i < sNotz.size(); i++) {
            if (CheckLists.isValidCheckList(sNotzData.getNote(sNotz.get(i).getAsJsonObject()))) {
                sb.append(sNotzWidgets.getWidgetText(sNotzData.getNote(sNotz.get(i).getAsJsonObject()))).append("\n... ... ... ... ...\n\n");
            } else {
                sb.append(sNotzData.getNote(sNotz.get(i).getAsJsonObject())).append("\n... ... ... ... ...\n\n");
            }
        }
        return sb.toString().trim();
    }

    public static boolean validBackup(String backupData) {
        return sNotzData.getJSONObject(backupData) != null && Objects.requireNonNull(sNotzData.getJSONObject(backupData)).getAsJsonArray("sNotz") != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getColoredDrawable(int color, int drawable, Context context) {
        Drawable d = ContextCompat.getDrawable(context, drawable);
        if (d != null) {
            d.setTint(color);
        }
        return d;
    }

    public static int generateNoteID(Context context) {
        int noteID = 0;
        for (sNotzItems items : sNotzData.getRawData(context)) {
            if (items.getNoteID() >= noteID) {
                noteID = items.getNoteID() + 1;
            }
        }
        return noteID;
    }

    public static void bitmapToPNG(Bitmap bitmap, File file) {
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException ignored) {}
    }

    public static void shareNote(String note, Context context) {
        Intent share_note = new Intent();
        share_note.setAction(Intent.ACTION_SEND);
        share_note.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.shared_by, BuildConfig.VERSION_NAME));
        share_note.putExtra(Intent.EXTRA_TEXT, "\"" + note + "\"\n\n" +
                context.getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
        share_note.setType("text/plain");
        Intent shareIntent = Intent.createChooser(share_note, context.getString(R.string.share_with));
        context.startActivity(shareIntent);
    }

    private static JsonObject getNote(String text, int colorBg, int colorTxt, int noteID, long date, boolean hidden) {
        JsonObject note = new JsonObject();
        note.addProperty("note", text);
        note.addProperty("date", date);
        note.addProperty("hidden", hidden);
        note.addProperty("colorBackground", colorBg);
        note.addProperty("colorText", colorTxt);
        note.addProperty("noteID", noteID);
        return note;
    }

    public static JsonObject getNote(int id, sNotzItems items) {
        JsonObject note = new JsonObject();
        note.addProperty("note", items.getNote());
        note.addProperty("date", items.getTimeStamp());
        note.addProperty("hidden", items.isHidden());
        note.addProperty("colorBackground", items.getColorBackground());
        note.addProperty("colorText", items.getColorText());
        note.addProperty("noteID", id != Integer.MIN_VALUE ? id : items.getNoteID());
        return note;
    }

    public static void updateDataBase(List<sNotzItems> data, Context context) {
        JsonObject mJSONObject = new JsonObject();
        JsonArray mJSONArray = new JsonArray();
        for (sNotzItems items : data) {
            mJSONArray.add(getNote(items.getNote(), items.getColorBackground(), items.getColorText(), items.getNoteID(), items.getTimeStamp(), items.isHidden()));
        }
        mJSONObject.add("sNotz", mJSONArray);
        Gson gson = new Gson();
        String json = gson.toJson(mJSONObject);
        sFileUtils.create(json, new File(context.getFilesDir(), "snotz"));
        Utils.updateWidgets(context);
    }

}