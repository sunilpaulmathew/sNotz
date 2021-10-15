package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    public static List<sNotzItems> getNotesFromBackup(String backupData, Context context) {
        List<sNotzItems> mRestoreData = new ArrayList<>();
        for (int i = 0; i < sNotzData.getsNotzItems(backupData).length(); i++) {
            try {
                JSONObject command = Objects.requireNonNull(sNotzData.getsNotzItems(backupData)).getJSONObject(i);
                mRestoreData.add(new sNotzItems(sNotzData.getNote(command.toString()), sNotzData.getDate(command.toString()), sNotzData.getImage(command.toString()), sNotzData.isHidden(command.toString()),
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
        
    public static int getMaxSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (Utils.getOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
            return displayMetrics.widthPixels / 3;
        } else {
            return displayMetrics.heightPixels / 3;
        }
    }

    public static int getColor(int color, Context context) {
        return ContextCompat.getColor(context, color);
    }

    public static Drawable getDrawable(int drawable, Context context) {
        return ContextCompat.getDrawable(context, drawable);
    }

    public static Drawable getColoredDrawable(int color, int drawable, Context context) {
        Drawable d = ContextCompat.getDrawable(context, drawable);
        if (d != null) {
            d.setTint(color);
        }
        return d;
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

    private static void bitmapToPNG(Bitmap bitmap, File file) {
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
            new AsyncTasks() {
                private final File mImageFile = new File(context.getExternalCacheDir(), "photo.png");

                @Override
                public void onPreExecute() {
                    Common.isWorking(true);
                    if (Utils.exist(mImageFile.toString())) {
                        Utils.delete(mImageFile.toString());
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

    public static AsyncTasks addNote(Editable newNote, String image, int colorBg, int colorTxt,
                               boolean hidden, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                try {
                    mJSONObject = new JSONObject(Objects.requireNonNull(Utils.read(context.getFilesDir().getPath() + "/snotz")));
                    mJSONArray = mJSONObject.getJSONArray("sNotz");
                    JSONObject note = new JSONObject();
                    note.put("note", newNote);
                    note.put("date", System.currentTimeMillis());
                    note.put("image", image);
                    note.put("hidden", hidden);
                    note.put("colorBackground", colorBg);
                    note.put("colorText", colorTxt);
                    note.put("noteID", sNotzData.getData(context).size());
                    mJSONArray.put(note);
                    Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static AsyncTasks deleteNote(int noteID, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                try {
                    mJSONObject = new JSONObject(Objects.requireNonNull(Utils.read(context.getFilesDir().getPath() + "/snotz")));
                    mJSONArray = mJSONObject.getJSONArray("sNotz");
                    mJSONArray.remove(noteID);
                    Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static AsyncTasks hideNote(int noteID, boolean hidden, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                try {
                    mJSONObject = new JSONObject(Objects.requireNonNull(Utils.read(context.getFilesDir().getPath() + "/snotz")));
                    mJSONArray = mJSONObject.getJSONArray("sNotz");
                    JSONObject note = new JSONObject(mJSONArray.getJSONObject(noteID).toString());
                    note.put("hidden", hidden);
                    mJSONArray.remove(noteID);
                    mJSONArray.put(note);
                    Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static AsyncTasks initializeNotes(Editable newNote, String image, int colorBg, int colorTxt,
                                             boolean hidden, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
                mJSONObject = new JSONObject();
                mJSONArray = new JSONArray();
            }

            @Override
            public void doInBackground() {
                try {
                    JSONObject note = new JSONObject();
                    note.put("note", newNote);
                    note.put("date", System.currentTimeMillis());
                    note.put("image", image);
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

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static AsyncTasks restoreNotes(String backupData, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
                mJSONObject = new JSONObject();
                mJSONArray = new JSONArray();
                i = 0;
            }

            @Override
            public void doInBackground() {
                try {
                    if (Utils.exist(context.getFilesDir().getPath() + "/snotz")) {
                        for (sNotzItems items : sNotzData.getRawData(context)) {
                            JSONObject note = new JSONObject();
                            note.put("note", items.getNote());
                            note.put("date", items.getTimeStamp());
                            note.put("image", items.getImageString());
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
                            note.put("image", items.getImageString());
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

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public static AsyncTasks updateNote(Editable newNote, String image, int noteID, int colorBg, int colorTxt,
                                        boolean hidden, ProgressBar progressBar, Context context) {
        return new AsyncTasks() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                try {
                    mJSONObject = new JSONObject(Objects.requireNonNull(Utils.read(context.getFilesDir().getPath() + "/snotz")));
                    mJSONArray = mJSONObject.getJSONArray("sNotz");
                    JSONObject note = new JSONObject(mJSONArray.getJSONObject(noteID).toString());
                    note.put("note", newNote);
                    note.put("date", System.currentTimeMillis());
                    note.put("image", image);
                    note.put("hidden", hidden);
                    note.put("colorBackground", colorBg);
                    note.put("colorText", colorTxt);
                    note.put("noteID", noteID);
                    mJSONArray.remove(noteID);
                    mJSONArray.put(note);
                    Utils.create(mJSONObject.toString(), context.getFilesDir().getPath() + "/snotz");
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onPostExecute() {
                Utils.reloadUI(context);
                Common.isWorking(false);
                progressBar.setVisibility(View.GONE);
            }
        };
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
                note.put("image", items.getImageString());
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