package com.sunilpaulmathew.snotz.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 30, 2021
 */
public class Encryption {

    public static String decrypt(String encryptedText) {
        byte[] data = Base64.decode(encryptedText, Base64.DEFAULT);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String encrypt(String text2Encrypt) {
        byte[] data = text2Encrypt.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

}