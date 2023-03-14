package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.activities.ImageViewActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.CommonUtils.sCommonUtils;
import in.sunilpaulmathew.sCommon.CommonUtils.sExecutor;
import in.sunilpaulmathew.sCommon.FileUtils.sFileUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 22, 2021
 */
public class QRCodeUtils {

    private final Activity mActivity;
    private final String mData;
    private final Uri mUri;

    public QRCodeUtils(String data, Uri uri, Activity activity) {
        this.mData = data;
        this.mUri = uri;
        this.mActivity = activity;
    }

    public sExecutor generateQRCode() {
        if (mData == null) return null;
        return new sExecutor() {
            private Bitmap mBitmap = null;

            @Override
            public void onPreExecute() {
                Common.isWorking(true);
            }

            @Override
            public void doInBackground() {
                try {
                    BitMatrix matrix = new MultiFormatWriter().encode(mData, BarcodeFormat.QR_CODE, 200, 200);
                    mBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
                    for (int x = 0; x < 200; x++) {
                        for (int y = 0; y < 200; y++) {
                            mBitmap.setPixel(x, y, matrix.get(x, y) ? sCommonUtils.getColor(R.color.color_black, mActivity) :
                                    sCommonUtils.getColor(R.color.color_white, mActivity));
                        }
                    }
                } catch (WriterException ignored) {}
            }

            @Override
            public void onPostExecute() {
                Common.isWorking(false);
                if (mBitmap != null) {
                    Common.setReadModeImage(mBitmap);
                    Intent imageView = new Intent(mActivity, ImageViewActivity.class);
                    mActivity.startActivity(imageView);
                } else {
                    sCommonUtils.snackBar(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.qr_code_generate_error_message)).show();
                }
            }
        };
    }

    public String readQRCode() {
        if (mUri == null) return null;
        try {
            InputStream inputStream = mActivity.getContentResolver().openInputStream(mUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                return null;
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(bBitmap);
            return result.getText();
        } catch (FileNotFoundException | NotFoundException ignored) {
        }
        return null;
    }

    public sExecutor shareQRCode(Bitmap bitmap) {
        return new sExecutor() {
            private final File mImageFile = new File(mActivity.getExternalCacheDir(), "photo.png");

            @Override
            public void onPreExecute() {
                Common.isWorking(true);
                if (sFileUtils.exist(mImageFile)) {
                    sFileUtils.delete(mImageFile);
                }
            }

            @Override
            public void doInBackground() {
                sNotzUtils.bitmapToPNG(bitmap, mImageFile);
            }

            @Override
            public void onPostExecute() {
                Common.isWorking(false);
                if (sFileUtils.exist(mImageFile)) {
                    Intent share_QRCode = new Intent();
                    share_QRCode.setAction(Intent.ACTION_SEND);
                    share_QRCode.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                    if (mData != null) {
                        String[] sNotzContents = mData.split("\\s+");
                        share_QRCode.putExtra(Intent.EXTRA_TEXT, sNotzContents.length <= 2 ? mData : sNotzContents[0] + " " + sNotzContents[1] + " " + sNotzContents[2] + "...");
                    }
                    Uri uri = FileProvider.getUriForFile(mActivity,BuildConfig.APPLICATION_ID + ".provider", mImageFile);
                    share_QRCode.putExtra(Intent.EXTRA_STREAM, uri);
                    share_QRCode.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share_QRCode.setType("image/png");
                    Intent shareIntent = Intent.createChooser(share_QRCode, mActivity.getString(R.string.share_with));
                    mActivity.startActivity(shareIntent);
                } else {
                    sCommonUtils.snackBar(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.qr_code_generate_error_message)).show();
                }
            }
        };
    }

    public void saveQRCode(Bitmap bitmap, String name) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                OutputStream imageOutStream;
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = mActivity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                imageOutStream = mActivity.getContentResolver().openOutputStream(uri);
                appendTextOnBitmap(bitmap, mActivity).compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                imageOutStream.close();
            } else {
                FileOutputStream imageOutStream;
                File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
                imageOutStream = new FileOutputStream(image);
                appendTextOnBitmap(bitmap, mActivity).compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                imageOutStream.close();
            }
        } catch(Exception ignored) {
        }
    }

    private static Bitmap appendTextOnBitmap(Bitmap bitmap, Activity activity) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(sCommonUtils.getColor(R.color.color_black, activity));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15);
        int horizontalSpacing = Objects.requireNonNull(bitmap).getWidth() / 2;
        int verticalSpacing = Objects.requireNonNull(bitmap).getHeight() - 5;
        String[] splitText = Common.getNote().split(" ");
        String newTxt;
        if (splitText.length >= 3) {
            newTxt = splitText[0] + " " + splitText[1] + " " + splitText[2] + "...";
        } else {
            newTxt = Common.getNote();
        }
        if (newTxt.length() > 10) {
            newTxt = newTxt.substring(0, 10) + "...";
        }
        canvas.drawText(newTxt, horizontalSpacing, verticalSpacing, paint);
        return bitmap;
    }

}