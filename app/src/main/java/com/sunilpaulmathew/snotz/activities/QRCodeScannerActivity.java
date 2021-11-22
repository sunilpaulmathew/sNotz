package com.sunilpaulmathew.snotz.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.sunilpaulmathew.snotz.utils.Common;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 22, 2021
 * Based on the original work of Dushyanth (@dm77) for Barcode Scanner
 * Ref: https://github.com/dm77/barcodescanner/blob/master/zxing-sample/src/main/java/me/dm7/barcodescanner/zxing/sample/SimpleScannerActivity.java
 */
public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Common.setExternalNote(rawResult.getText());
        Intent scanner = new Intent(QRCodeScannerActivity.this, CreateNoteActivity.class);
        startActivity(scanner);
        finish();
    }

}