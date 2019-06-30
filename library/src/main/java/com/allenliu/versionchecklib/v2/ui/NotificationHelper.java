package com.allenliu.versionchecklib.v2.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.ui.MaskDialogActivity;
import com.allenliu.versionchecklib.utils.UpgradeUtil;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;

import java.io.File;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {

    public static final int NOTIFICATION_ID = 1;

    private static final String CHANNEL_ID = "version_service_id";

    private DownloadBuilder versionBuilder;
    private NotificationCompat.Builder notificationBuilder = null;
    private NotificationManager mManager = null;
    private int mCurrentProgress;

    private Context mContext;

    public NotificationHelper(DownloadBuilder builder) {
        mContext = AllenVersionChecker.getInstance().getContext();
        this.versionBuilder = builder;
        mCurrentProgress = 0;
    }

    /**
     * 显示通知栏
     */
    public void showNotification() {
        if (!versionBuilder.isShowNotification()) {
            return;
        }

        mManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = createNotification();
        mManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * 更新下载进度的通知
     */
    public void updateNotification(int progress) {
        if (!versionBuilder.isShowNotification()) {
            return;
        }

        if ((progress - mCurrentProgress) > 5) {
            notificationBuilder.setContentIntent(null);
            NotificationBuilder libNotificationBuilder = versionBuilder.getNotificationBuilder();
            String contentText = mContext.getString(R.string.upgrade_download_progress);
            if (libNotificationBuilder.getContentText() != null) {
                contentText = libNotificationBuilder.getContentText();
            }
            notificationBuilder.setContentText(String.format(contentText, progress));
            notificationBuilder.setProgress(100, progress, false);
            mManager.notify(NOTIFICATION_ID, notificationBuilder.build());
            mCurrentProgress = progress;
        }
    }

    /**
     * 显示下载完成的通知
     */
    public void showDownloadCompleteNotifcation(File file) {
        if (!versionBuilder.isShowNotification()) {
            return;
        }

        Intent intent = UpgradeUtil.buildInstallApkIntent(file);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentText(mContext.getString(R.string.upgrade_download_install));
        notificationBuilder.setProgress(100, 100, false);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * 显示下载失败的通知
     */
    public void showDownloadFailedNotification() {
        if (!versionBuilder.isShowNotification()) {
            return;
        }

        Intent intent = new MaskDialogActivity.Builder(mContext)
                .setDownloadFailedType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentText(UpgradeUtil.getString(R.string.upgrade_download_fail_retry));
        notificationBuilder.setProgress(100, 0, false);

        mManager.cancelAll();
        mManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private NotificationCompat.Builder createNotification() {
        final String CHANNEL_ID = "0";
        final String CHANNEL_NAME = "ALLEN_NOTIFICATION";

        NotificationCompat.Builder builder;
        NotificationBuilder libNotificationBuilder = versionBuilder.getNotificationBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setSmallIcon(versionBuilder.getNotificationBuilder().getIcon());
        //设置标题
        String contentTitle = UpgradeUtil.getString(R.string.app_name);
        if (libNotificationBuilder.getContentTitle() != null) {
            contentTitle = libNotificationBuilder.getContentTitle();
        }
        builder.setContentTitle(contentTitle);
        //设置ticker
        String ticker = UpgradeUtil.getString(R.string.upgrade_downloading);
        if (libNotificationBuilder.getTicker() != null) {
            ticker = libNotificationBuilder.getTicker();
        }
        builder.setTicker(ticker);
        //设置内容
        String contentText = mContext.getString(R.string.upgrade_download_progress);
        if (libNotificationBuilder.getContentText() != null) {
            contentText = libNotificationBuilder.getContentText();
        }
        builder.setContentText(String.format(contentText, 0));

        if (libNotificationBuilder.isRingtone()) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(mContext, notification);
            ringtone.play();
        }

        return builder;
    }

    public void onDestroy() {
        if (mManager != null) {
            mManager.cancel(NOTIFICATION_ID);
        }
    }

    public Notification getServiceNotification() {
        NotificationCompat.Builder notifcationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(UpgradeUtil.getString(R.string.app_name))
                .setContentText(UpgradeUtil.getString(R.string.upgrade_service_running))
                .setSmallIcon(versionBuilder.getNotificationBuilder().getIcon())
                .setAutoCancel(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "version_service_name", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        return notifcationBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification createSimpleNotification() {
        Context context = AllenVersionChecker.getInstance().getContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyApp", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("")
                .build();
    }

}