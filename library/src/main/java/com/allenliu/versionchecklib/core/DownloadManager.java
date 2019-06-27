package com.allenliu.versionchecklib.core;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.core.http.FileCallBack;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class DownloadManager {

    private static int lastProgress = 0;
    private static boolean isDownloadSuccess = false;

    public static void downloadAPK(final String url, final VersionParams versionParams, final DownloadListener listener) {
        lastProgress = 0;
        isDownloadSuccess = false;

        if (url == null || url.isEmpty()) {
            return;
        }

        final Context context = AllenVersionChecker.getInstance().getContext();
        File downloadFile = new File(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName()));
        String downloadPath = downloadFile.getAbsolutePath();
        //静默下载也判断本地是否有缓存
        if (versionParams.isSilentDownload()) {
            if (!versionParams.isForceRedownload()) {
                //判断本地文件是否存在
                if (checkAPKIsExists(context, downloadPath)) {
                    if (listener != null)
                        listener.onCheckerDownloadSuccess(new File(downloadPath));
                    return;
                }
                silentDownloadAPK(context, url, versionParams, listener);

            } else {
                silentDownloadAPK(context, url, versionParams, listener);
            }
            return;
        }

        if (!versionParams.isForceRedownload()) {
            //判断本地文件是否存在
            if (checkAPKIsExists(context, downloadPath)) {
                if (listener != null)
                    listener.onCheckerDownloadSuccess(new File(downloadPath));
                AppUtils.installApk(context, new File(downloadPath));
                return;

            }
        }
        if (listener != null) {
            listener.onCheckerStartDownload();
        }
        NotificationCompat.Builder builder = null;
        NotificationManager manager = null;
        if (versionParams.isShowNotification()) {
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            builder = createNotification(context);
            manager.notify(0, builder.build());
        }
        final NotificationCompat.Builder finalBuilder = builder;
        final NotificationManager finalManager = manager;
        Request request = new Request.Builder().url(url).build();
        AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                listener.onCheckerDownloadSuccess(file);
                isDownloadSuccess = true;
                if (versionParams.isShowNotification()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);

                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    } else {
                        uri = Uri.fromFile(file);
                    }

                    //设置intent的类型
                    i.setDataAndType(uri, "application/vnd.android.package-archive");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
                    finalBuilder.setContentIntent(pendingIntent);
                    finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_finish));
                    finalBuilder.setProgress(100, 100, false);
                    finalManager.cancelAll();
                    finalManager.notify(0, finalBuilder.build());
                }

                AppUtils.installApk(context, file);
            }

            @Override
            public void onDownloading(int progress) {
                int currentProgress = progress;
                listener.onCheckerDownloading(currentProgress);

                if (currentProgress - lastProgress >= 5) {
                    lastProgress = currentProgress;
                    if (versionParams.isShowNotification() && !isDownloadSuccess) {
                        finalBuilder.setContentIntent(null);
                        finalBuilder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), lastProgress));
                        finalBuilder.setProgress(100, lastProgress, false);
                        finalManager.notify(0, finalBuilder.build());
                    }
                }
            }

            @Override
            public void onDownloadFailed() {
                if (versionParams.isShowNotification()) {
                    Intent intent = new Intent(context, versionParams.getCustomDownloadActivityClass());
                    intent.putExtra("isRetry", true);
                    intent.putExtra(AVersionService.VERSION_PARAMS_KEY, versionParams);
                    intent.putExtra("downloadUrl", url);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
                    finalBuilder.setContentIntent(pendingIntent);
                    finalBuilder.setContentText(context.getString(R.string.versionchecklib_download_fail));
                    finalBuilder.setProgress(100, 0, false);
                    finalManager.notify(0, finalBuilder.build());
                }
                listener.onCheckerDownloadFail();
            }

        });
    }

    private static void silentDownloadAPK(final Context context, String url, final VersionParams versionParams, final DownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        if (listener != null) {
            listener.onCheckerStartDownload();
        }
        AllenHttp.getHttpClient().newCall(request).enqueue(new FileCallBack(versionParams.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName())) {

            @Override
            public void onSuccess(File file, Call call, Response response) {
                listener.onCheckerDownloadSuccess(file);
            }

            @Override
            public void onDownloading(int progress) {
                int currentProgress = progress;
                if (currentProgress - lastProgress >= 5) {
                    lastProgress = currentProgress;
                }
                listener.onCheckerDownloading(currentProgress);
            }

            @Override
            public void onDownloadFailed() {
                listener.onCheckerDownloadFail();
            }

        });
    }

    public static boolean checkAPKIsExists(Context context, String downloadPath) {
        return checkAPKIsExists(context, downloadPath, null);
    }

    /**
     * @param context
     * @param downloadPath
     * @param newestVersionCode 开发者认为的最新的版本号
     * @return
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

    private static NotificationCompat.Builder createNotification(Context context) {
        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setContentTitle(context.getString(R.string.app_name));

        builder.setTicker(context.getString(R.string.versionchecklib_downloading));
        builder.setContentText(String.format(context.getString(R.string.versionchecklib_download_progress), 0));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        return builder;
    }

}