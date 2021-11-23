package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

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
                            mBitmap.setPixel(x, y, matrix.get(x, y) ? sUtils.getColor(R.color.color_black, mActivity) :
                                    sUtils.getColor(R.color.color_white, mActivity));
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
                    sUtils.snackBar(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.qr_code_generate_error_message)).show();
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
                if (sUtils.exist(mImageFile)) {
                    sUtils.delete(mImageFile);
                }
            }

            @Override
            public void doInBackground() {
                sNotzUtils.bitmapToPNG(bitmap, mImageFile);
            }

            @Override
            public void onPostExecute() {
                Common.isWorking(false);
                if (sUtils.exist(mImageFile)) {
                    Intent share_QRCode = new Intent();
                    share_QRCode.setAction(Intent.ACTION_SEND);
                    share_QRCode.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                    Uri uri = FileProvider.getUriForFile(mActivity,BuildConfig.APPLICATION_ID + ".provider", mImageFile);
                    share_QRCode.putExtra(Intent.EXTRA_STREAM, uri);
                    share_QRCode.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share_QRCode.setType("image/png");
                    Intent shareIntent = Intent.createChooser(share_QRCode, mActivity.getString(R.string.share_with));
                    mActivity.startActivity(shareIntent);
                } else {
                    sUtils.snackBar(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.qr_code_generate_error_message)).show();
                }
            }
        };
    }

    public void saveQRCode(Bitmap bitmap, String name) {
        try {
            OutputStream imageOutStream;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = mActivity.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                imageOutStream = mActivity.getContentResolver().openOutputStream(uri);
            } else {
                File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
                imageOutStream = new FileOutputStream(image);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
            imageOutStream.close();
        } catch(Exception ignored) {
        }
    }

}