package com.sunilpaulmathew.snotz.utils;

import android.os.Build;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 30, 2021
 */
public class Encryption {

    public static String decrypt(String encryptedText) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            byte[] data = Base64.decode(encryptedText, Base64.DEFAULT);
            return new String(data, StandardCharsets.UTF_8);
        } else {
            return encryptedText;
        }
    }

    public static String encrypt(String text2Encrypt) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            byte[] data = text2Encrypt.getBytes(StandardCharsets.UTF_8);
            return Base64.encodeToString(data, Base64.DEFAULT);
        } else {
            return text2Encrypt;
        }

    }

}