package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;

import com.allenliu.versionchecklib.callback.OnCheckDownloadListener;
import com.allenliu.versionchecklib.http.HttpClient;
import com.allenliu.versionchecklib.http.FileCallBack;
import com.allenliu.versionchecklib.UpgradeClient;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class UpgradeUtil {

    /**
     * 获取包名
     */
    public static String getPackageName() {
        return UpgradeClient.getInstance().getContext().getPackageName();
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager pm = UpgradeClient.getInstance().getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return pi == null ? -1 : pi.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取资源文字
     */
    public static String getString(@StringRes int resId) {
        return UpgradeClient.getInstance().getContext().getString(resId);
    }

    /**
     * 获取资源文字
     */
    public static String getString(@StringRes int resId, Object... formatArgs) {
        return UpgradeClient.getInstance().getContext().getString(resId, formatArgs);
    }

    /**
     * 创建安装apk的Intent
     */
    public static Intent buildInstallApkIntent(File file) {
        Context context = UpgradeClient.getInstance().getContext();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        return intent;
    }

    /**
     * 安装apk
     */
    public static void installApk(File file) {
        Context context = UpgradeClient.getInstance().getContext();
        Intent intent = buildInstallApkIntent(file);
        context.startActivity(intent);
    }

    /**
     * 获取下载的文件目录
     */
    public static String getDownloadDir() {
        Context context = UpgradeClient.getInstance().getContext();

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

    /**
     * 下载apk
     */
    public static void download(final String url, final String downloadApkPath, final String fileName, final OnCheckDownloadListener listener) {
        if (url != null && !url.isEmpty()) {
            Handler handler = new Handler(Looper.getMainLooper());

            Request request = new Request
                    .Builder()
                    .addHeader("Accept-Encoding", "identity")
                    .url(url)
                    .build();

            handler.post(new Runnable() {

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onCheckerStartDownload();
                    }
                }

            });

            HttpClient.getHttpClient().newCall(request).enqueue(new FileCallBack(handler, downloadApkPath, fileName) {

                @Override
                public void onSuccess(final File file, Call call, Response response) {
                    getHandle().post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCheckerDownloadSuccess(file);
                            }
                        }

                    });
                }

                @Override
                public void onDownloading(final int progress) {
                    getHandle().post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCheckerDownloading(progress);
                            }
                        }

                    });
                }

                @Override
                public void onDownloadFailed() {
                    getHandle().post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onCheckerDownloadFail();
                            }
                        }

                    });
                }

            });

        } else {
            throw new RuntimeException("you must set download url for download function using");
        }
    }

    /**
     * 检查apk是否存在
     *
     * @param apkPath           apk文件存储地址
     * @param newestVersionCode 开发者认为的最新的版本号
     */
    public static boolean checkApkExist(String apkPath, Integer newestVersionCode) {
        File file = new File(apkPath);

        if (file.exists()) {
            try {
                PackageManager pm = UpgradeClient.getInstance().getContext().getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);

                if (getPackageName().equalsIgnoreCase(info.packageName)) {//相同包名
                    if (getVersionCode() < info.versionCode) {//安装包的版本号大于当前app的版本号
                        if (newestVersionCode != null && info.versionCode == newestVersionCode) {//传入的最新版本号等于安装包的版本号
                            return true;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean checkApkExist(String apkPath) {
        return checkApkExist(apkPath, null);
    }

}