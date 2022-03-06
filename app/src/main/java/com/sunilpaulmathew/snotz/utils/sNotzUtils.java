package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 01, 2021
 */
public class sNotzUtils {

    private static final String mExternalNote = "externalNote";

    public static List<sNotzItems> getNotesFromBackup(String backupData, Context context) {
        List<sNotzItems> mRestoreData = new ArrayList<>();
        JsonArray sNotz = Objects.requireNonNull(sNotzData.getJSONObject(backupData)).getAsJsonArray("sNotz");
        for (int i = 0; i < sNotz.size(); i++) {
            mRestoreData.add(new sNotzItems(sNotzData.getNote(sNotz.get(i).getAsJsonObject()),
                    sNotzData.getDate(sNotz.get(i).getAsJsonObject()),
                    sNotzData.getImage(sNotz.get(i).getAsJsonObject()),
                    sNotzData.isHidden(sNotz.get(i).getAsJsonObject()),
                    sNotzData.getBackgroundColor(sNotz.get(i).getAsJsonObject(), context),
                    sNotzData.getTextColor(sNotz.get(i).getAsJsonObject(), context),
                    sNotzData.getNoteID(sNotz.get(i).getAsJsonObject()))
            );
        }
        return mRestoreData;
    }

    public static String getExternalNote() {
        return mExternalNote;
    }

    public static String sNotzToText(Context context) {
        StringBuilder sb = new StringBuilder();
        JsonArray sNotz = Objects.requireNonNull(sNotzData.getJSONObject(sUtils.read(new File(context.getFilesDir(), "snotz")))).getAsJsonArray("sNotz");
        for (int i = 0; i < sNotz.size(); i++) {
            sb.append(sNotzData.getNote(sNotz.get(i).getAsJsonObject())).append("\n... ... ... ... ...\n\n");
        }
        return sb.toString();
    }

    public static boolean validBackup(String backupData) {
        return sNotzData.getJSONObject(backupData) != null && Objects.requireNonNull(sNotzData
                .getJSONObject(backupData)).getAsJsonArray("sNotz") != null;
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
        
    public static int getMaxSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (sUtils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
            return displayMetrics.widthPixels / 3;
        } else {
            return displayMetrics.heightPixels / 3;
        }
    }

    public static String bitmapToBase64(Bitmap bitmap, Activity activity) {
        try {
            int size = getMaxSize(activity);
            float ratio = Math.min((float) size / bitmap.getWidth(), (float) size / bitmap.getHeight());
            int width = Math.round(ratio * bitmap.getWidth());
            int height = Math.round(ratio * bitmap.getHeight());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap.createScaledBitmap(bitmap, width, height, true).compress(Bitmap
                    .CompressFormat.PNG,100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception ignored) {}
        return null;
    }

    public static Bitmap stringToBitmap(String string) {
        try {
            byte[] imageAsBytes = Base64.decode(string.getBytes(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        } catch (Exception ignored) {}
        return null;
    }

    public static void bitmapToPNG(Bitmap bitmap, File file) {
        try {
            OutputStream outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException ignored) {}
    }

    public static void shareNote(String note, String imageString, Context context) {
        Intent share_note = new Intent();
        share_note.setAction(Intent.ACTION_SEND);
        share_note.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.shared_by, BuildConfig.VERSION_NAME));
        share_note.putExtra(Intent.EXTRA_TEXT, "\"" + note + "\"\n\n" +
                context.getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
        if (imageString != null) {
            new sExecutor() {
                private final File mImageFile = new File(context.getExternalCacheDir(), "photo.png");

                @Override
                public void onPreExecute() {
                    Common.isWorking(true);
                    if (sUtils.exist(mImageFile)) {
                        sUtils.delete(mImageFile);
                    }
                }

                @Override
                public void doInBackground() {
                    sNotzUtils.bitmapToPNG(Objects.requireNonNull(sNotzUtils.stringToBitmap(imageString)), mImageFile);
                    Uri uri = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".provider", mImageFile);
                    share_note.putExtra(Intent.EXTRA_STREAM, uri);
                    share_note.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                @Override
                public void onPostExecute() {
                    share_note.setType("image/png");
                    Intent shareIntent = Intent.createChooser(share_note, context.getString(R.string.share_with));
                    context.startActivity(shareIntent);
                    Common.isWorking(false);
                }
            }.execute();
        } else {
            share_note.setType("text/plain");
            Intent shareIntent = Intent.createChooser(share_note, context.getString(R.string.share_with));
            context.startActivity(shareIntent);
        }
    }

    public static void addNote(Editable newNote, String image, int colorBg, int colorTxt,
                                    boolean hidden, ProgressBar progressBar, Context context) {
        addNote(newNote, image, colorBg, colorTxt, hidden, false, progressBar, context).execute();

    }

    public static sExecutor addNote(Editable newNote, String image, int colorBg, int colorTxt,
                               boolean hidden, boolean autoSave, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                if (!autoSave) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = sNotzData.getJSONObject(sUtils.read(new File(context.getFilesDir(),"snotz")));
                JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");
                JsonObject note = new JsonObject();
                note.addProperty("note", newNote.toString());
                note.addProperty("date", System.currentTimeMillis());
                note.addProperty("image", image);
                note.addProperty("hidden", hidden);
                note.addProperty("colorBackground", colorBg);
                note.addProperty("colorText", colorTxt);
                note.addProperty("noteID", generateNoteID(context));
                mJSONArray.add(note);
                mJSONObject.add("sNotz", mJSONArray);
                Gson gson = new Gson();
                String json = gson.toJson(mJSONObject);
                sUtils.create(json, new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                if (!autoSave) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    public static sExecutor deleteNote(int noteID, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = sNotzData.getJSONObject(sUtils.read(new File(context.getFilesDir(),"snotz")));
                JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");

                for (int i = 0; i < mJSONArray.size(); i++) {
                    JsonObject note = mJSONArray.get(i).getAsJsonObject();
                    if (note.get("noteID").getAsInt() == noteID) {
                        mJSONArray.remove(i);
                    }
                }
                mJSONObject.add("sNotz", mJSONArray);
                Gson gson = new Gson();
                String json = gson.toJson(mJSONObject);
                sUtils.create(json, new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static sExecutor hideNote(int noteID, boolean hidden, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = sNotzData.getJSONObject(sUtils.read(new File(context.getFilesDir(),"snotz")));
                JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");

                for (int i = 0; i < mJSONArray.size(); i++) {
                    JsonObject note = mJSONArray.get(i).getAsJsonObject();
                    if (note.get("noteID").getAsInt() == noteID) {
                        mJSONArray.remove(i);
                        note.addProperty("hidden", hidden);
                        mJSONArray.add(note);
                    }
                }
                mJSONObject.add("sNotz", mJSONArray);
                Gson gson = new Gson();
                String json = gson.toJson(mJSONObject);
                sUtils.create(json, new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static void initializeNotes(Editable newNote, String image, int colorBg, int colorTxt,
                                            boolean hidden, ProgressBar progressBar, Context context) {
        initializeNotes(newNote, image, colorBg, colorTxt, hidden, false, progressBar, context).execute();
    }

    public static sExecutor initializeNotes(Editable newNote, String image, int colorBg, int colorTxt,
                                             boolean hidden, boolean autoSave, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                if (!autoSave) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = new JsonObject();
                JsonArray mJSONArray = new JsonArray();
                JsonObject note = new JsonObject();
                note.addProperty("note", newNote.toString());
                note.addProperty("date", System.currentTimeMillis());
                note.addProperty("image", image);
                note.addProperty("hidden", hidden);
                note.addProperty("colorBackground", colorBg);
                note.addProperty("colorText", colorTxt);
                note.addProperty("noteID", 0);
                mJSONArray.add(note);
                mJSONObject.add("sNotz", mJSONArray);
                Gson gson = new Gson();
                String json = gson.toJson(mJSONObject);
                sUtils.create(json, new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                if (!autoSave) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    public static sExecutor restoreNotes(String backupData, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            private int i = 0;
            @Override
            public void onPreExecute() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = new JsonObject();
                JsonArray mJSONArray = new JsonArray();
                if (sUtils.exist(new File(context.getFilesDir(),"snotz"))) {
                    for (sNotzItems items : sNotzData.getRawData(context)) {
                        JsonObject note = new JsonObject();
                        note.addProperty("note", items.getNote());
                        note.addProperty("date", items.getTimeStamp());
                        note.addProperty("image", items.getImageString());
                        note.addProperty("hidden", items.isHidden());
                        note.addProperty("colorBackground", items.getColorBackground());
                        note.addProperty("colorText", items.getColorText());
                        note.addProperty("noteID", items.getNoteID());
                        mJSONArray.add(note);
                    }
                    i = generateNoteID(context);
                }

                if (validBackup(backupData)) {
                    for (sNotzItems items : getNotesFromBackup(backupData, context)) {
                        JsonObject note = new JsonObject();
                        note.addProperty("note", items.getNote());
                        note.addProperty("date", items.getTimeStamp());
                        note.addProperty("image", items.getImageString());
                        note.addProperty("hidden", items.isHidden());
                        note.addProperty("colorBackground", items.getColorBackground());
                        note.addProperty("colorText", items.getColorText());
                        note.addProperty("noteID", i);
                        i++;
                        mJSONArray.add(note);
                    }
                }
                mJSONObject.add("sNotz", mJSONArray);
                sUtils.create(mJSONObject.toString(), new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    public static void updateNote(Editable newNote, String image, int noteID, int colorBg, int colorTxt,
                                       boolean hidden, ProgressBar progressBar, Context context) {
        updateNote(newNote, image, noteID, colorBg, colorTxt, hidden, false, progressBar, context).execute();
    }

    public static sExecutor updateNote(Editable newNote, String image, int noteID, int colorBg, int colorTxt,
                                        boolean hidden, boolean autoSave, ProgressBar progressBar, Context context) {
        return new sExecutor() {
            @Override
            public void onPreExecute() {
                if (!autoSave) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                JsonObject mJSONObject = sNotzData.getJSONObject(sUtils.read(new File(context.getFilesDir(),"snotz")));
                JsonArray mJSONArray = Objects.requireNonNull(mJSONObject).getAsJsonArray("sNotz");

                JsonObject mNote = new JsonObject();
                mNote.addProperty("note", newNote.toString());
                mNote.addProperty("date", System.currentTimeMillis());
                mNote.addProperty("image", image);
                mNote.addProperty("hidden", hidden);
                mNote.addProperty("colorBackground", colorBg);
                mNote.addProperty("colorText", colorTxt);
                mNote.addProperty("noteID", noteID);
                for (int i = 0; i < mJSONArray.size(); i++) {
                    JsonObject note = mJSONArray.get(i).getAsJsonObject();
                    if (note.get("noteID").getAsInt() == noteID) {
                        mJSONArray.remove(i);
                        mJSONArray.add(mNote);
                    }
                }
                mJSONObject.add("sNotz", mJSONArray);
                Gson gson = new Gson();
                String json = gson.toJson(mJSONObject);
                sUtils.create(json, new File(context.getFilesDir(),"snotz"));
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                if (!autoSave) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

}