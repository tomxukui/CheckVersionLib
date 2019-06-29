package com.allenliu.versionchecklib.v2.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.http.AllenHttp;
import com.allenliu.versionchecklib.http.FileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadMangerV2 {

    public static void download(final String url, final String downloadApkPath, final String fileName, final DownloadListener listener) {
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

            AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(handler, downloadApkPath, fileName) {

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

    public static boolean checkAPKIsExists(Context context, String downloadPath) {
        return checkAPKIsExists(context, downloadPath, null);
    }

    /**
     * @param context
     * @param downloadPath
     * @param newestVersionCode 开发者认为的最新的版本号
     */
    public static boolean checkAPKIsExists(Context context, String downloadPath, Integer newestVersionCode) {
        File file = new File(downloadPath);
        boolean result = false;
        if (file.exists()) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(downloadPath, PackageManager.GET_ACTIVITIES);

                //判断安装包存在并且包名一样并且版本号不一样
                if (context.getPackageName().equalsIgnoreCase(info.packageName) && context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode != info.versionCode) {
                    //判断开发者传入的最新版本号是否大于缓存包的版本号，大于那么相当于没有缓存
                    if (newestVersionCode != null && info.versionCode < newestVersionCode) {
                        result = false;

                    } else {
                        result = true;
                    }
                }

            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }

}