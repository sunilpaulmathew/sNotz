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

import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.interfaces.DialogEditTextListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 14, 2021
 */
public class AppSettings {

    private static int getFontSizePosition(Context context) {
        String value = String.valueOf(Utils.getInt("font_size", 18, context));
        for (int i = 0; i < getFontSizes().length; i++) {
            if (getFontSizes()[i].contains(value)) {
                return i;
            }
        }
        return 0;
    }

    private static int getFontStylePosition(Context context) {
        String style = Utils.getString("font_style", "bold|italic", context);
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
        String style = Utils.getString("font_style", "bold|italic", context);
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

    public static String getFontStyle(Context context) {
        String style = Utils.getString("font_style", "bold|italic", context);
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

    private static String[] getFontSizes() {
        return new String[]{"10sp", "11sp", "12sp", "13sp", "14sp", "15sp", "16sp", "17sp", "18sp", "19sp", "20sp",
                "21sp", "22sp", "23sp", "24sp", "25sp"};
    }

    private static String[] getFontStyles(Context context) {
        return new String[]{context.getString(R.string.text_style_regular), context.getString(R.string.text_style_italics),
                context.getString(R.string.text_style_bold), context.getString(R.string.text_style_bold_italics)};
    }

    private static String[] getBackupOptions(Context context) {
        return new String[]{context.getString(R.string.backup_snotz), context.getString(R.string.save_text)};
    }

    public static void setFontSize(int position, List<SettingsItems> items, SettingsAdapter adapter, Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.font_size)
                .setSingleChoiceItems(getFontSizes(), getFontSizePosition(context), (dialog, itemPosition) -> {
                    Utils.saveInt("font_size", Integer.parseInt(getFontSizes()[itemPosition].replace("sp","")), context);
                    items.set(position, new SettingsItems(context.getString(R.string.font_size), context.getString(R.string.font_size_summary,
                            "" + Integer.parseInt(getFontSizes()[itemPosition].replace("sp",""))),
                            sNotzUtils.getDrawable(R.drawable.ic_format_size, context), null));
                    adapter.notifyItemChanged(position);
                    Utils.reloadUI(context);
                    dialog.dismiss();
                }).show();
    }

    public static void setFontStyle(int position, List<SettingsItems> items, SettingsAdapter adapter, Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.text_style)
                .setSingleChoiceItems(getFontStyles(context), getFontStylePosition(context), (dialog, itemPosition) -> {
                    Utils.saveString("font_style", getFontStyle(itemPosition), context);
                    items.set(position, new SettingsItems(context.getString(R.string.text_style), getFontStyle(context),
                            sNotzUtils.getDrawable(R.drawable.ic_text_style, context), null));
                    adapter.notifyItemChanged(position);
                    Utils.reloadUI(context);
                    dialog.dismiss();
                }).show();
    }

    public static void showBackupOptions(Activity activity) {
        new MaterialAlertDialogBuilder(activity)
                .setTitle(R.string.backup_notes)
                .setSingleChoiceItems(getBackupOptions(activity), 0, (dialog, itemPosition) -> {
                    if (itemPosition == 0) {
                        saveDialog(Encryption.encrypt(Objects.requireNonNull(Utils.read(activity.getFilesDir().getPath() + "/snotz"))), activity);
                    } else {
                        if (Utils.getBoolean("allow_images", false, activity)) {
                            Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.image_excluded_warning));
                        }
                        saveDialog(sNotzUtils.sNotzToText(activity), activity);
                    }
                    dialog.dismiss();
                }).show();
    }

    private static void saveDialog(String sNotz, Activity activity) {
        if (Build.VERSION.SDK_INT < 29 && Utils.isPermissionDenied(activity)) {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        DialogEditTextListener.dialogEditText("sNotz", activity.getString(R.string.backup_notes_hint),
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.text_empty));
                        return;
                    }
                    if (!text.endsWith(".txt")) {
                        text += ".txt";
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    String fileName = text;
                    if (Utils.exist(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text)) {
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
            Utils.create(sNotz, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text);
        }
        Utils.showSnackbar(activity.findViewById(android.R.id.content), activity.getString(R.string.backup_notes_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text));
    }

}