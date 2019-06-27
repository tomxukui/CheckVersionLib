package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.os.Environment;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;

import java.io.File;

public class FileHelper {

    /**
     * 获取存储apk的缓存地址
     */
    public static String getDownloadApkCachePath() {
        Context context = AllenVersionChecker.getInstance().getContext();

        File file = null;
        if (checkSDCard()) {
            file = context.getExternalFilesDir("apks");

        } else {
            file = new File(context.getFilesDir(), "apks");
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    /**
     * 判断是否存在sdcard
     */
    public static boolean checkSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}