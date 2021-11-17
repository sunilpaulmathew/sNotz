package com.sunilpaulmathew.snotz.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sSerializableItems;
import in.sunilpaulmathew.sCommon.Utils.sSingleChoiceDialog;
import in.sunilpaulmathew.sCommon.Utils.sSingleItemDialog;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 14, 2021
 */
public class AppSettings {

    private static int getRowPosition(Context context) {
        return sUtils.getInt("span_count", 0, context);
    }

    private static int getFontSizePosition(Context context) {
        for (int i = 0; i < getFontSizes().length; i++) {
            if (sUtils.getInt("font_size", 18, context) == Integer.parseInt(
                    getFontSizes()[i].replace("sp",""))) {
                return i;
            }
        }
        return 0;
    }

    private static int getFontStylePosition(Context context) {
        String style = sUtils.getString("font_style", "bold|italic", context);
        switch (style) {
            case "regular":
                return 0;
            case "italics":
                return 1;
            case "bold":
                return 2;
            default:
                return 3;
        }
    }

    public static int getStyle(Context context) {
        String style = sUtils.getString("font_style", "bold|italic", context);
        switch (style) {
            case "regular":
                return Typeface.NORMAL;
            case "italics":
                return Typeface.ITALIC;
            case "bold":
                return Typeface.BOLD;
            default:
                return Typeface.BOLD_ITALIC;
        }
    }

    public static List<sSerializableItems> getCredits() {
        List<sSerializableItems> mData = new ArrayList<>();

        mData.add(new sSerializableItems(null, "Grarak", "Code contributions", "https://github.com/Grarak/"));
        mData.add(new sSerializableItems(null, "Lennoard Silva", "Code contributions & Portuguese (Brazilian) Translations", "https://github.com/Lennoard/"));
        mData.add(new sSerializableItems(null, "Clone Conflict" , "Code contributions & Arabic Translations", "https://github.com/cloneconf/"));
        mData.add(new sSerializableItems(null, "QuadFlask", "Color Picker", "https://github.com/QuadFlask/colorpicker/"));

        mData.add(new sSerializableItems(null, "MONSTER_PC", "Ukrainian & Russian Translations", "https://t.me/MONSTER_PC"));
        mData.add(new sSerializableItems(null, "Sshsmnv2000", "Russian Translations", null));
        mData.add(new sSerializableItems(null, "anonymous", "Russian Translations", null));
        mData.add(new sSerializableItems(null, "Hafitz Setya", "Indonesian Translations", "https://github.com/breakdowns/"));
        mData.add(new sSerializableItems(null, "Mikesew1320", "Amharic Translations", "https://github.com/Mikesew1320/"));
        mData.add(new sSerializableItems(null, "Negroibarra301", "Spanish Translations", null));
        mData.add(new sSerializableItems(null, "el-leo-pardo", "Spanish Translations", "https://github.com/el-leo-pardo/"));
        mData.add(new sSerializableItems(null, "FTno", "Norwegian Translations", "https://github.com/FTno/"));
        mData.add(new sSerializableItems(null, "Murilogs", "Portuguese (Brazilian) Translations", null));
        mData.add(new sSerializableItems(null, "Axel Schaab", "German Translations", null));
        mData.add(new sSerializableItems(null, "AbsurdUsername","Italian Translations", "https://github.com/AbsurdUsername/"));
        mData.add(new sSerializableItems(null, "Reno", "French Translations", "https://t.me/Renoooooo"));
        mData.add(new sSerializableItems(null, "Emrehelvaci83", "Turkish Translations", null));
        mData.add(new sSerializableItems(null, "Bo Lindholm", "Swedish Translations", null));
        mData.add(new sSerializableItems(null, "jaswinder77", "Hindi Translations & Testing", "https://github.com/jaswinder77/"));

        mData.add(new sSerializableItems(null, "Alfie", "Testing", "https://t.me/AlfieFie"));

        return mData;
    }

    public static String getFontStyle(Context context) {
        String style = sUtils.getString("font_style", "bold|italic", context);
        switch (style) {
            case "regular":
                return context.getString(R.string.text_style_regular);
            case "italics":
                return context.getString(R.string.text_style_italics);
            case "bold":
                return context.getString(R.string.text_style_bold);
            default:
                return context.getString(R.string.text_style_bold_italics);
        }
    }

    private static String getFontStyle(int position) {
        switch (position) {
            case 0:
                return "regular";
            case 1:
                return "italics";
            case 2:
                return "bold";
            default:
                return "bold|italic";
        }
    }

    public static String getRows(Context context) {
        int rows = sUtils.getInt("span_count", 0, context);
        switch (rows) {
            case 1:
                return context.getString(R.string.notes_in_row_summary, "1");
            case 2:
                return context.getString(R.string.notes_in_row_summary, "2");
            case 3:
                return context.getString(R.string.notes_in_row_summary, "3");
            case 4:
                return context.getString(R.string.notes_in_row_summary, "4");
            case 5:
                return context.getString(R.string.notes_in_row_summary, "5");
            default:
                return context.getString(R.string.notes_in_row_default);
        }
    }

    private static String[] getFontSizes() {
        return new String[] {
                "10sp", "11sp", "12sp", "13sp", "14sp", "15sp", "16sp", "17sp", "18sp", "19sp", "20sp",
                "21sp", "22sp", "23sp", "24sp", "25sp"
        };
    }

    public static void setRows(Activity activity) {
        new sSingleChoiceDialog(R.drawable.ic_row, activity.getString(R.string.notes_in_row),
                new String[] {
                        activity.getString(R.string.notes_in_row_default),
                        activity.getString(R.string.notes_in_row_summary, "1"),
                        activity.getString(R.string.notes_in_row_summary, "2"),
                        activity.getString(R.string.notes_in_row_summary, "3"),
                        activity.getString(R.string.notes_in_row_summary, "4"),
                        activity.getString(R.string.notes_in_row_summary, "5")
                }, getRowPosition(activity), activity) {

            @Override
            public void onItemSelected(int itemPosition) {
                sUtils.saveInt("span_count", itemPosition, activity);
                Utils.restartApp(activity);
            }
        }.show();
    }

    public static void setFontSize(int position, List<SettingsItems> items, SettingsAdapter adapter, Context context) {
        new sSingleChoiceDialog(R.drawable.ic_format_size, context.getString(R.string.font_size),
                getFontSizes(), getFontSizePosition(context), context) {

            @Override
            public void onItemSelected(int itemPosition) {
                sUtils.saveInt("font_size", Integer.parseInt(getFontSizes()[itemPosition].replace("sp","")), context);
                items.set(position, new SettingsItems(context.getString(R.string.font_size), context.getString(R.string.font_size_summary,
                        "" + Integer.parseInt(getFontSizes()[itemPosition].replace("sp",""))),
                        sUtils.getDrawable(R.drawable.ic_format_size, context), null));
                adapter.notifyItemChanged(position);
                Utils.reloadUI(context);
            }
        }.show();
    }

    public static void setFontStyle(int position, List<SettingsItems> items, SettingsAdapter adapter, Context context) {
        new sSingleChoiceDialog(R.drawable.ic_text_style, context.getString(R.string.text_style),
                new String[] {
                        context.getString(R.string.text_style_regular),
                        context.getString(R.string.text_style_italics),
                        context.getString(R.string.text_style_bold),
                        context.getString(R.string.text_style_bold_italics)
                }, getFontStylePosition(context), context) {

            @Override
            public void onItemSelected(int itemPosition) {
                sUtils.saveString("font_style", getFontStyle(itemPosition), context);
                items.set(position, new SettingsItems(context.getString(R.string.text_style), getFontStyle(context),
                        sUtils.getDrawable(R.drawable.ic_text_style, context), null));
                adapter.notifyItemChanged(position);
                Utils.reloadUI(context);
            }
        }.show();
    }

    public static void showBackupOptions(Activity activity) {
        new sSingleItemDialog(R.drawable.ic_backup, activity.getString(R.string.backup_notes),
                new String[] {
                        activity.getString(R.string.backup_snotz),
                        activity.getString(R.string.save_text)
                }, activity) {

            @Override
            public void onItemSelected(int itemPosition) {
                if (itemPosition == 0) {
                    saveDialog(Encryption.encrypt(Objects.requireNonNull(sUtils.read(new File(activity.getFilesDir(),"snotz")))), activity);
                } else {
                    if (sUtils.getBoolean("allow_images", false, activity)) {
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.image_excluded_warning)).show();
                    }
                    saveDialog(sNotzUtils.sNotzToText(activity), activity);
                }
            }
        }.show();
    }

    private static void saveDialog(String sNotz, Activity activity) {
        if (Build.VERSION.SDK_INT < 29 && sPermissionUtils.isPermissionDenied(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, activity)) {
            sPermissionUtils.requestPermission(new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },activity);
            return;
        }
        DialogEditTextListener.dialogEditText("sNotz", activity.getString(R.string.backup_notes_hint),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.text_empty)).show();
                        return;
                    }
                    if (!text.endsWith(".txt")) {
                        text += ".txt";
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    String fileName = text;
                    if (sUtils.exist(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), text))) {
                        new MaterialAlertDialogBuilder(activity)
                                .setMessage(activity.getString(R.string.backup_notes_warning))
                                .setNegativeButton(activity.getString(R.string.change_name), (dialogInterface, i) -> saveDialog(sNotz, activity))
                                .setPositiveButton(activity.getString(R.string.replace), (dialogInterface, i) -> save(sNotz, fileName, activity)).show();
                        return;
                    }
                    save(sNotz, fileName, activity);
                }, -1, activity).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    private static void save(String sNotz, String text, Activity activity) {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = activity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
                outputStream.write(sNotz.getBytes());
                outputStream.close();
            } catch (IOException ignored) {
            }
        } else {
            sUtils.create(sNotz, new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), text));
        }
        sUtils.snackBar(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_notes_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text)).show();
    }

}