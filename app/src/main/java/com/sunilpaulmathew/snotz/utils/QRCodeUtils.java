package com.sunilpaulmathew.snotz.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

    public sExecutor shareQRCode() {
        if (mData == null) return null;
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
                try {
                    BitMatrix matrix = new MultiFormatWriter().encode(mData, BarcodeFormat.QR_CODE, 200, 200);
                    Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
                    for (int x = 0; x < 200; x++) {
                        for (int y = 0; y < 200; y++) {
                            bitmap.setPixel(x, y, matrix.get(x, y) ? sUtils.getColor(R.color.color_black, mActivity) :
                                    sUtils.getColor(R.color.color_white, mActivity));
                        }
                    }
                    sNotzUtils.bitmapToPNG(bitmap, mImageFile);
                } catch (WriterException ignored) {}
            }

            @Override
            public void onPostExecute() {
                Intent share_QRCode = new Intent();
                share_QRCode.setAction(Intent.ACTION_SEND);
                share_QRCode.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.shared_by, BuildConfig.VERSION_NAME));
                share_QRCode.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                Uri uri = FileProvider.getUriForFile(mActivity,BuildConfig.APPLICATION_ID + ".provider", mImageFile);
                share_QRCode.putExtra(Intent.EXTRA_STREAM, uri);
                share_QRCode.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share_QRCode.setType("image/png");
                Intent shareIntent = Intent.createChooser(share_QRCode, mActivity.getString(R.string.share_with));
                mActivity.startActivity(shareIntent);
                Common.isWorking(false);
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

}