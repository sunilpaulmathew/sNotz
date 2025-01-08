package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.os.Build;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.utils.serializableItems.sNotzItems;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class sNotzData {

    public static List<sNotzItems> getData(Context context, String searchText) {
        List<sNotzItems> mData = new CopyOnWriteArrayList<>();
        for (sNotzItems items : getRawData(context)) {
            boolean isVisible;
            if (sCommonUtils.getInt("show_all", 0, context) == 2) {
                isVisible = !items.isChecklist();
            } else if (sCommonUtils.getInt("show_all", 0, context) == 1) {
                isVisible = items.isChecklist();
            } else {
                isVisible = true;
            }
            if (isVisible) {
                if (searchText == null) {
                    if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                        mData.add(items);
                    } else if (!items.isHidden()) {
                        mData.add(items);
                    }
                } else if (Common.isTextMatched(items.getNote(), searchText)) {
                    if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                        mData.add(items);
                    } else if (!items.isHidden()) {
                        mData.add(items);
                    }
                }
            }
        }
        if (sCommonUtils.getInt("sort_notes", 2, context) == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(mData, Comparator.comparingLong(sNotzItems::getTimeStamp));
            } else {
                Collections.sort(mData, (lhs, rhs) -> String.CASE_INSENSITIVE_ORDER.compare(String.valueOf(lhs.getTimeStamp()), String.valueOf(rhs.getTimeStamp())));
            }
        } else if (sCommonUtils.getInt("sort_notes", 2, context) == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(mData, Comparator.comparingLong(sNotzItems::getColorBackground));
            } else {
                Collections.sort(mData, (lhs, rhs) -> String.CASE_INSENSITIVE_ORDER.compare(String.valueOf(lhs.getColorBackground()), String.valueOf(rhs.getColorBackground())));
            }
        } else {
            Collections.sort(mData, (lhs, rhs) -> String.CASE_INSENSITIVE_ORDER.compare(rhs.getNote(), lhs.getNote()));
        }
        if (!sCommonUtils.getBoolean("reverse_order", false, context)) {
            Collections.reverse(mData);
        }
        return mData;
    }

    public static List<sNotzItems> getRawData(Context context) {
        List<sNotzItems> mData = new CopyOnWriteArrayList<>();
        String json = context.getFilesDir().getPath() + "/snotz";
        if (sFileUtils.exist(new File(json))) {
            JsonArray sNotz = Objects.requireNonNull(getJSONObject(sFileUtils.read(new File(json)))).getAsJsonArray("sNotz");
            for (int i = 0; i < sNotz.size(); i++) {
                mData.add(new sNotzItems(
                                getNote(sNotz.get(i).getAsJsonObject()),
                                getDate(sNotz.get(i).getAsJsonObject()),
                                isHidden(sNotz.get(i).getAsJsonObject()),
                                getBackgroundColor(sNotz.get(i).getAsJsonObject(), context),
                                getTextColor(sNotz.get(i).getAsJsonObject(), context),
                                getNoteID(sNotz.get(i).getAsJsonObject())
                        )
                );
            }
        }
        return mData;
    }

    public static boolean isNotesEmpty(Context context) {
        return !sFileUtils.exist(new File(context.getFilesDir(),"snotz")) ||
                (sFileUtils.exist(new File(context.getFilesDir(),"snotz")) &&
                        getRawData(context).isEmpty());
    }

    public static JsonObject getJSONObject(String string) {
        try {
            return JsonParser.parseString(string).getAsJsonObject();
        } catch (Exception ignored) {}
        return null;
    }

    public static String getNote(JsonObject object) {
        return object.get("note").getAsString();
    }

    public static long getDate(JsonObject object) {
        try {
            return object.get("date").getAsLong();
        } catch (Exception ignored) {
        }
        return System.currentTimeMillis();
    }

    public static boolean isHidden(JsonObject object) {
        try {
            return object.get("hidden").getAsBoolean();
        } catch (Exception ignored) {
        }
        return false;
    }

    public static int getBackgroundColor(JsonObject object, Context context) {
        try {
            return object.get("colorBackground").getAsInt();
        } catch (Exception ignored) {
        }
        return sCommonUtils.getInt("accent_color", sCommonUtils.getColor(R.color.color_teal, context), context);
    }

    public static int getTextColor(JsonObject object, Context context) {
        try {
            return object.get("colorText").getAsInt();
        } catch (Exception ignored) {
        }
        return sCommonUtils.getInt("text_color", sCommonUtils.getColor(R.color.color_white, context), context);
    }

    public static int getNoteID(JsonObject object) {
        try {
            return object.get("noteID").getAsInt();
        } catch (Exception ignored) {
        }
        return -1;
    }

}