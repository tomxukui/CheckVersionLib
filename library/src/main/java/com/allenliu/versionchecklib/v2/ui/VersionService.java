package com.allenliu.versionchecklib.v2.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.callback.DownloadListener;
import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.event.DownloadingProgressEvent;
import com.allenliu.versionchecklib.event.UpgradeEvent;
import com.allenliu.versionchecklib.ui.MaskDialogActivity;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.net.DownloadMangerV2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VersionService extends Service {

    public static DownloadBuilder builder;

    private BuilderHelper builderHelper;
    private NotificationHelper notificationHelper;
    private boolean isServiceAlive = false;

    private ExecutorService mExecutorService;

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
        builderHelper = null;
        if (notificationHelper != null) {
            notificationHelper.onDestroy();
        }
        notificationHelper = null;
        isServiceAlive = false;
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
        stopForeground(true);
        AllenHttp.getHttpClient().dispatcher().cancelAll();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.createSimpleNotification(this));

        if (builder != null) {
            isServiceAlive = true;
            builderHelper = new BuilderHelper(builder);
            notificationHelper = new NotificationHelper(getApplicationContext(), builder);

            startForeground(NotificationHelper.NOTIFICATION_ID, notificationHelper.getServiceNotification());

            mExecutorService = Executors.newSingleThreadExecutor();
            mExecutorService.submit(new Runnable() {

                @Override
                public void run() {
                    downloadAPK();
                }

            });

        } else {
            AllenVersionChecker.getInstance().cancelAllMission();
        }
    }

    /**
     * 显示版本对话框
     */
    private void showVersionDialog() {
        if (builder != null) {
            Intent intent = new MaskDialogActivity.Builder(this)
                    .setVersionType()
                    .create()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog() {
        if (builder != null && builder.isShowDownloadingDialog()) {
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
        if (builder != null) {
            Intent intent = new MaskDialogActivity.Builder(this)
                    .setDownloadFailedType()
                    .create()
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    /**
     * 下载apk
     */
    private void downloadAPK() {
        if (builder != null && builder.getVersionBundle() != null) {
            if (builder.isDirectDownload()) {
                startDownloadApk();

            } else {
                if (builder.isSilentDownload()) {
                    startDownloadApk();

                } else {
                    showVersionDialog();
                }
            }

        } else {
            AllenVersionChecker.getInstance().cancelAllMission();
        }
    }

    /**
     * 安装apk
     */
    private void install() {
        AppUtils.installApk(getApplicationContext(), getDownloadFile(), builder.getCustomInstallListener());
    }

    public static void enqueueWork(final Context context) {
        Intent intent = new Intent(context, VersionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);

        } else {
            context.startService(intent);
        }
    }

//    private void install() {
//        AllenEventBusUtil.sendEventBus(AllenEventType.DOWNLOAD_COMPLETE);
//
//        final String downloadPath = getDownloadFilePath();
//        if (builder.isSilentDownload()) {
//            showVersionDialog();
//
//        } else {
//            AppUtils.installApk(getApplicationContext(), new File(downloadPath), builder.getCustomInstallListener());
//            builderHelper.checkForceUpdate();
//        }
//    }

    /**
     * 获取下载的apk文件
     */
    private File getDownloadFile() {
        return new File(builder.getDownloadAPKPath(), getString(R.string.versionchecklib_download_apkname, builder.getApkName() != null ? builder.getApkName() : getPackageName()));
    }

    @WorkerThread
    private void startDownloadApk() {
        //判断是否缓存并且是否强制重新下载
        final String downloadPath = getDownloadFile().getAbsolutePath();
        if (DownloadMangerV2.checkAPKIsExists(getApplicationContext(), downloadPath, builder.getNewestVersionCode()) && !builder.isForceRedownload()) {
            install();
            return;
        }

        builderHelper.checkAndDeleteAPK();
        String downloadUrl = builder.getDownloadUrl();
        if (downloadUrl == null && builder.getVersionBundle() != null) {
            downloadUrl = builder.getVersionBundle().getDownloadUrl();
        }
        if (downloadUrl == null) {
            AllenVersionChecker.getInstance().cancelAllMission();
            throw new RuntimeException("you must set a download url for download function using");
        }

        DownloadMangerV2.download(downloadUrl, builder.getDownloadAPKPath(), getString(R.string.versionchecklib_download_apkname, builder.getApkName() != null ? builder.getApkName() : getPackageName()), new DownloadListener() {

            @Override
            public void onCheckerDownloading(int progress) {
                if (isServiceAlive && builder != null) {
                    if (!builder.isSilentDownload()) {
                        notificationHelper.updateNotification(progress);
                        updateDownloadingDialogProgress(progress);
                    }
                    if (builder.getApkDownloadListener() != null) {
                        builder.getApkDownloadListener().onDownloading(progress);
                    }
                }
            }

            @Override
            public void onCheckerDownloadSuccess(File file) {
                if (isServiceAlive) {
                    if (!builder.isSilentDownload()) {
                        notificationHelper.showDownloadCompleteNotifcation(file);
                    }
                    if (builder.getApkDownloadListener() != null) {
                        builder.getApkDownloadListener().onDownloadSuccess(file);
                    }

                    install();
                }
            }

            @Override
            public void onCheckerDownloadFail() {
                if (!isServiceAlive) {
                    return;
                }
                if (builder.getApkDownloadListener() != null) {
                    builder.getApkDownloadListener().onDownloadFail();
                }

                if (!builder.isSilentDownload()) {
                    if (builder.isShowDownloadFailDialog()) {
                        showDownloadFailedDialog();
                    }
                    notificationHelper.showDownloadFailedNotification();

                } else {
                    AllenVersionChecker.getInstance().cancelAllMission();
                }
            }

            @Override
            public void onCheckerStartDownload() {
                if (!builder.isSilentDownload()) {
                    notificationHelper.showNotification();
                    showDownloadingDialog();
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

            case UpgradeEvent.CANCEL_UPGRADE: //用户取消更新
            case UpgradeEvent.CANCEL_DOWNLOADING://用户取消下载
            {
                builderHelper.checkForceUpdate();
                AllenVersionChecker.getInstance().cancelAllMission();

                OnCancelListener listener = builder.getOnCancelListener();
                if (listener != null) {
                    listener.onCancel();
                }
            }
            break;

            default:
                break;

        }
    }

}