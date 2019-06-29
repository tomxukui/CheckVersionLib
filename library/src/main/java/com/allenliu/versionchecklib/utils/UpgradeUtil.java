package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.StringRes;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;

import java.io.File;

public class UpgradeUtil {

    /**
     * 获取资源文字
     */
    public static String getString(@StringRes int resId) {
        return AllenVersionChecker.getInstance().getContext().getString(resId);
    }

    /**
     * 安装apk
     */
    public static void installApk(File file) {
        Context context = AllenVersionChecker.getInstance().getContext();

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取下载的文件目录
     */
    public static String getDownloadDir() {
        Context context = AllenVersionChecker.getInstance().getContext();

        File file;
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