package com.sunilpaulmathew.snotz.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;

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
        Intent scanner = new Intent(QRCodeScannerActivity.this, NoteActivity.class);
        scanner.putExtra(NoteActivity.NOTE_INTENT, rawResult.getText());
        scanner.putExtra(NoteActivity.NOTE_ID_INTENT, -1);
        addItem.launch(scanner);
    }

    private final ActivityResultLauncher<Intent> addItem = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent intent = result.getData();
                    intent.putExtra("note", intent.getStringExtra("note"));
                    intent.putExtra("hidden", intent.getBooleanExtra("hidden", false));
                    intent.putExtra("colorBackground", intent.getIntExtra("colorBackground", Integer.MIN_VALUE));
                    intent.putExtra("colorText", intent.getIntExtra("colorText", Integer.MIN_VALUE));
                    intent.putExtra("id", intent.getIntExtra("id", Integer.MIN_VALUE));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
    );

}