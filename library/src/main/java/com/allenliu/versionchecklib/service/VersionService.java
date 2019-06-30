package com.allenliu.versionchecklib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.allenliu.versionchecklib.callback.OnCheckDownloadListener;
import com.allenliu.versionchecklib.http.HttpClient;
import com.allenliu.versionchecklib.event.DownloadingProgressEvent;
import com.allenliu.versionchecklib.event.UpgradeEvent;
import com.allenliu.versionchecklib.ui.MaskDialogActivity;
import com.allenliu.versionchecklib.utils.UpgradeUtil;
import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.builder.DownloadBuilder;
import com.allenliu.versionchecklib.utils.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionService extends Service {

    public static DownloadBuilder builder;

    private ExecutorService mExecutorService;
    private NotificationHelper notificationHelper;

    private boolean mIsServiceAlive = false;//服务是否存在
    private boolean mIsDownloadComplete = false;//下载是否已完成

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (notificationHelper != null) {
            notificationHelper.onDestroy();
        }

        notificationHelper = null;
        mIsServiceAlive = false;
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        stopForeground(true);
        HttpClient.getHttpClient().dispatcher().cancelAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化
     */
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.createSimpleNotification());
        }

        if (builder == null) {
            UpgradeClient.getInstance().cancelAllMission();
            return;
        }

        mIsServiceAlive = true;
        notificationHelper = new NotificationHelper(builder);

        startForeground(NotificationHelper.NOTIFICATION_ID, notificationHelper.getServiceNotification());

        mExecutorService = Executors.newSingleThreadExecutor();
        mExecutorService.submit(new Runnable() {

            @Override
            public void run() {
                downloadAPK();
            }

        });
    }

    /**
     * 显示版本对话框
     */
    private void showVersionDialog() {
        Intent intent = new MaskDialogActivity.Builder(this)
                .setVersionType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog() {
        if (builder.isShowDownloadingDialog()) {
            Intent intent = new MaskDialogActivity.Builder(this)
                    .setDownloadingType()
                    .create()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    /**
     * 更新下载进度
     */
    private void updateDownloadingDialogProgress(int progress) {
        EventBus.getDefault().post(new DownloadingProgressEvent(progress));
    }

    /**
     * 显示下载失败对话框
     */
    private void showDownloadFailedDialog() {
        Intent intent = new MaskDialogActivity.Builder(this)
                .setDownloadFailedType()
                .create()
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    /**
     * 下载apk
     */
    private void downloadAPK() {
        if (builder.isSilentDownload()) {
            startDownloadApk();

        } else {
            showVersionDialog();
        }
    }

    /**
     * 安装apk
     */
    private void install() {
        UpgradeUtil.installApk(builder.getApkFile());
    }

    @WorkerThread
    private void startDownloadApk() {
        //判断是否缓存并且是否强制重新下载
        if (!builder.isForceRedownload()) {
            if (UpgradeUtil.checkApkExist(builder.getApkFile())) {
                install();
                return;
            }
        }

        //如果存在该文件, 则先删除
        UpgradeUtil.deleteFile(builder.getApkFile());

        //准备下载apk
        mIsDownloadComplete = false;
        UpgradeUtil.download(builder.getUpgradeInfo().getDownloadUrl(), builder.getApkDir(), builder.getApkName(), new OnCheckDownloadListener() {

            @Override
            public void onCheckerStartDownload() {
                if (!builder.isSilentDownload()) {
                    notificationHelper.showNotification();
                    showDownloadingDialog();
                }
            }

            @Override
            public void onCheckerDownloading(int progress) {
                if (!mIsServiceAlive) {
                    return;
                }

                if (!builder.isSilentDownload()) {
                    notificationHelper.updateNotification(progress);
                    updateDownloadingDialogProgress(progress);
                }

                if (builder.getOnDownloadListener() != null) {
                    builder.getOnDownloadListener().onDownloading(progress);
                }
            }

            @Override
            public void onCheckerDownloadSuccess(File file) {
                mIsDownloadComplete = true;

                if (!mIsServiceAlive) {
                    return;
                }

                if (!builder.isSilentDownload()) {
                    notificationHelper.showDownloadCompleteNotifcation(file);
                }

                if (builder.getOnDownloadListener() != null) {
                    builder.getOnDownloadListener().onDownloadSuccess(file);
                }

                install();
            }

            @Override
            public void onCheckerDownloadFail() {
                if (!mIsServiceAlive) {
                    return;
                }
                if (builder.getOnDownloadListener() != null) {
                    builder.getOnDownloadListener().onDownloadFail();
                }

                if (!builder.isSilentDownload()) {
                    if (builder.isShowDownloadFailDialog()) {
                        showDownloadFailedDialog();
                    }
                    notificationHelper.showDownloadFailedNotification();

                } else {
                    UpgradeClient.getInstance().cancelAllMission();
                }
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeEvent(UpgradeEvent event) {
        switch (event.type) {

            case UpgradeEvent.CONFIRM_UPGRADE: {//用户同意更新
                startDownloadApk();
            }
            break;

            case UpgradeEvent.DOWNLOAD_COMPLETE: {//下载已完成
                install();
            }
            break;

            case UpgradeEvent.RETRY_DOWNLOAD: {//重新下载
                startDownloadApk();
            }
            break;

            case UpgradeEvent.CANCEL_UPGRADE: {//用户取消更新
                UpgradeClient.getInstance().cancelAllMission();

                if (builder.getOnCancelListener() != null) {
                    builder.getOnCancelListener().onCancel(builder.getUpgradeInfo());
                }
            }
            break;

            case UpgradeEvent.CANCEL_DOWNLOADING: {//用户取消下载
                HttpClient.getHttpClient().dispatcher().cancelAll();

                if (mIsDownloadComplete) {
                    showVersionDialog();

                } else {
                    showDownloadFailedDialog();
                }
            }
            break;

            case UpgradeEvent.CANCEL_RETRY_DOWNLOAD: {//用户取消重试
                showVersionDialog();
            }
            break;

            default:
                break;

        }
    }

    public static void enqueueWork() {
        Context context = UpgradeClient.getInstance().getContext();

        Intent intent = new Intent(context, VersionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);

        } else {
            context.startService(intent);
        }
    }

}