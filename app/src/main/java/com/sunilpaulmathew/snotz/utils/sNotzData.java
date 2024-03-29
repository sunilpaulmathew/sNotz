package com.sunilpaulmathew.snotz.utils;

import android.content.Context;
import android.os.Build;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunilpaulmathew.snotz.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class sNotzData {

    public static List<sNotzItems> getData(Context context) {
        List<sNotzItems> mData = new ArrayList<>();
        for (sNotzItems items : getRawData(context)) {
            if (Common.getSearchText() == null) {
                if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                    mData.add(items);
                } else if (!items.isHidden()) {
                    mData.add(items);
                }
            } else if (Common.isTextMatched(items.getNote())) {
                if (sCommonUtils.getBoolean("hidden_note", false, context)) {
                    mData.add(items);
                } else if (!items.isHidden()) {
                    mData.add(items);
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
        List<sNotzItems> mData = new ArrayList<>();
        if (sCommonUtils.getInt("show_all", 0, context) != 1) {
            String json = context.getFilesDir().getPath() + "/snotz";
            if (sFileUtils.exist(new File(json))) {
                JsonArray sNotz = Objects.requireNonNull(getJSONObject(sFileUtils.read(new File(json)))).getAsJsonArray("sNotz");
                for (int i = 0; i < sNotz.size(); i++) {
                    mData.add(new sNotzItems(getNote(sNotz.get(i).getAsJsonObject()),
                            getDate(sNotz.get(i).getAsJsonObject()),
                            getImage(sNotz.get(i).getAsJsonObject()),
                            isHidden(sNotz.get(i).getAsJsonObject()),
                            getBackgroundColor(sNotz.get(i).getAsJsonObject(), context),
                            getTextColor(sNotz.get(i).getAsJsonObject(), context),
                            getNoteID(sNotz.get(i).getAsJsonObject()))
                    );
                }
            }
        }
        if (sCommonUtils.getInt("show_all", 0, context) != 2) {
            for (File checklists : Objects.requireNonNull(context.getExternalFilesDir("checklists").listFiles())) {
                if (CheckLists.isValidCheckList(sFileUtils.read(checklists))) {
                    mData.add(new sNotzItems(checklists.getAbsolutePath(),
                            checklists.lastModified(),
                            null,
                            false,
                            sCommonUtils.getColor(R.color.color_black, context),
                            sCommonUtils.getColor(R.color.color_white, context),
                            -1)
                    );
                }
            }
        }
        return mData;
    }

    public static boolean isNotesEmpty(Context context) {
        return !sFileUtils.exist(new File(context.getFilesDir(),"snotz")) ||
                (sFileUtils.exist(new File(context.getFilesDir(),"snotz")) &&
                        getRawData(context).size() == 0);
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

    public static String getImage(JsonObject object) {
        try {
            return object.get("image").getAsString();
        } catch (Exception ignored) {
        }
        return null;
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