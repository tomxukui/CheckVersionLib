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
    NotificationCompat.Builder notificationBuilder = null;
    NotificationManager manager = null;
    private boolean isDownloadSuccess = false;
    private boolean isFailed = false;
    private int currentProgress;
    private String contentText;

    private Context mContext;

    public NotificationHelper(DownloadBuilder builder) {
        mContext = AllenVersionChecker.getInstance().getContext();
        this.versionBuilder = builder;
        currentProgress = 0;
    }

    /**
     * update notification progress
     *
     * @param progress the progress of notification
     */
    public void updateNotification(int progress) {
        if (versionBuilder.isShowNotification()) {
            if ((progress - currentProgress) > 5 && !isDownloadSuccess && !isFailed) {
                notificationBuilder.setContentIntent(null);
                notificationBuilder.setContentText(String.format(contentText, progress));
                notificationBuilder.setProgress(100, progress, false);
                manager.notify(NOTIFICATION_ID, notificationBuilder.build());
                currentProgress = progress;
            }
        }
    }

    /**
     * show notification
     */
    public void showNotification() {
        isDownloadSuccess = false;
        isFailed = false;
        if (versionBuilder.isShowNotification()) {
            manager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            notificationBuilder = createNotification();
            manager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    /**
     * show download success notification
     */
    public void showDownloadCompleteNotifcation(File file) {
        isDownloadSuccess = true;

        if (!versionBuilder.isShowNotification()) {
            return;
        }

        Intent intent = UpgradeUtil.buildInstallApkIntent(file);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentText(mContext.getString(R.string.versionchecklib_download_finish));
        notificationBuilder.setProgress(100, 100, false);
        manager.cancelAll();
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void showDownloadFailedNotification() {
        isDownloadSuccess = false;
        isFailed = true;
        if (versionBuilder.isShowNotification()) {
            Intent intent = new MaskDialogActivity.Builder(mContext)
                    .create()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setContentText(UpgradeUtil.getString(R.string.upgrade_download_fail_retry));
            notificationBuilder.setProgress(100, 0, false);
            manager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private NotificationCompat.Builder createNotification() {
        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
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
        //set content title
        String contentTitle = mContext.getString(R.string.app_name);
        if (libNotificationBuilder.getContentTitle() != null)
            contentTitle = libNotificationBuilder.getContentTitle();
        builder.setContentTitle(contentTitle);
        //set ticker
        String ticker = mContext.getString(R.string.versionchecklib_downloading);
        if (libNotificationBuilder.getTicker() != null)
            ticker = libNotificationBuilder.getTicker();
        builder.setTicker(ticker);
        //set content text
        contentText = mContext.getString(R.string.versionchecklib_download_progress);
        if (libNotificationBuilder.getContentText() != null)
            contentText = libNotificationBuilder.getContentText();
        builder.setContentText(String.format(contentText, 0));

        if (libNotificationBuilder.isRingtone()) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, notification);
            r.play();
        }

        return builder;
    }

    public void onDestroy() {
        if (manager != null) {
            manager.cancel(NOTIFICATION_ID);
        }
    }

    public Notification getServiceNotification() {
        NotificationCompat.Builder notifcationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(mContext.getString(R.string.versionchecklib_version_service_runing))
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
    public static Notification createSimpleNotification(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyApp", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("")
                .build();
    }

}