package com.allenliu.versionchecklib.builder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.allenliu.versionchecklib.callback.ApkDownloadListener;
import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.utils.UpgradeUtil;
import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.callback.CustomDownloadFailedListener;
import com.allenliu.versionchecklib.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.http.RequestVersionManager;
import com.allenliu.versionchecklib.service.VersionService;

public class DownloadBuilder {

    private RequestVersionBuilder requestVersionBuilder;
    private boolean isSilentDownload;
    private String downloadAPKPath;
    private boolean isForceRedownload;
    private String downloadUrl;
    private boolean isShowDownloadingDialog;
    private boolean isShowNotification;
    private boolean isShowDownloadFailDialog;
    private boolean isDirectDownload;
    private NotificationBuilder notificationBuilder;
    private ApkDownloadListener apkDownloadListener;

    private CustomDownloadFailedListener customDownloadFailedListener;
    private CustomDownloadingDialogListener customDownloadingDialogListener;
    private CustomVersionDialogListener customVersionDialogListener;
    private OnCancelListener onCancelListener;
    private ForceUpdateListener forceUpdateListener;
    private UIData versionBundle;
    private Integer newestVersionCode;
    private String apkName;

    public DownloadBuilder(RequestVersionBuilder requestVersionBuilder, UIData versionBundle) {
        this.requestVersionBuilder = requestVersionBuilder;
        this.versionBundle = versionBundle;
        initialize();
    }

    private void initialize() {
        isSilentDownload = false;
        downloadAPKPath = UpgradeUtil.getDownloadDir();
        isForceRedownload = true;
        isShowDownloadingDialog = true;
        isShowNotification = true;
        isDirectDownload = false;
        isShowDownloadFailDialog = true;
        notificationBuilder = NotificationBuilder.create();
    }

    public ForceUpdateListener getForceUpdateListener() {
        return forceUpdateListener;
    }

    public DownloadBuilder setForceUpdateListener(ForceUpdateListener forceUpdateListener) {
        this.forceUpdateListener = forceUpdateListener;
        return this;
    }

    public DownloadBuilder setApkName(String apkName) {
        this.apkName = apkName;
        return this;
    }

    public DownloadBuilder setVersionBundle(@NonNull UIData versionBundle) {
        this.versionBundle = versionBundle;
        return this;
    }

    public UIData getVersionBundle() {
        return versionBundle;
    }

    public DownloadBuilder setOnCancelListener(OnCancelListener cancelListener) {
        this.onCancelListener = cancelListener;
        return this;
    }

    public DownloadBuilder setCustomDownloadFailedListener(CustomDownloadFailedListener customDownloadFailedListener) {
        this.customDownloadFailedListener = customDownloadFailedListener;
        return this;
    }

    public DownloadBuilder setCustomDownloadingDialogListener(CustomDownloadingDialogListener customDownloadingDialogListener) {
        this.customDownloadingDialogListener = customDownloadingDialogListener;
        return this;
    }

    public DownloadBuilder setCustomVersionDialogListener(CustomVersionDialogListener customVersionDialogListener) {
        this.customVersionDialogListener = customVersionDialogListener;
        return this;
    }

    public DownloadBuilder setSilentDownload(boolean silentDownload) {
        isSilentDownload = silentDownload;
        return this;
    }

    public Integer getNewestVersionCode() {
        return newestVersionCode;
    }

    public DownloadBuilder setNewestVersionCode(Integer newestVersionCode) {
        this.newestVersionCode = newestVersionCode;
        return this;
    }

    public DownloadBuilder setDownloadAPKPath(String downloadAPKPath) {
        this.downloadAPKPath = downloadAPKPath;
        return this;
    }

    public DownloadBuilder setForceRedownload(boolean forceRedownload) {
        isForceRedownload = forceRedownload;
        return this;
    }

    public DownloadBuilder setDownloadUrl(@NonNull String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public DownloadBuilder setShowDownloadingDialog(boolean showDownloadingDialog) {
        isShowDownloadingDialog = showDownloadingDialog;
        return this;
    }

    public DownloadBuilder setShowNotification(boolean showNotification) {
        isShowNotification = showNotification;
        return this;
    }

    public DownloadBuilder setShowDownloadFailDialog(boolean showDownloadFailDialog) {
        isShowDownloadFailDialog = showDownloadFailDialog;
        return this;
    }

    public DownloadBuilder setApkDownloadListener(ApkDownloadListener apkDownloadListener) {
        this.apkDownloadListener = apkDownloadListener;
        return this;
    }

    public boolean isSilentDownload() {
        return isSilentDownload;
    }

    public String getDownloadAPKPath() {
        return downloadAPKPath;
    }

    public boolean isForceRedownload() {
        return isForceRedownload;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isShowDownloadingDialog() {
        return isShowDownloadingDialog;
    }

    public boolean isShowNotification() {
        return isShowNotification;
    }

    public boolean isShowDownloadFailDialog() {
        return isShowDownloadFailDialog;
    }

    public ApkDownloadListener getApkDownloadListener() {
        return apkDownloadListener;
    }

    public CustomDownloadFailedListener getCustomDownloadFailedListener() {
        return customDownloadFailedListener;
    }

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public CustomDownloadingDialogListener getCustomDownloadingDialogListener() {
        return customDownloadingDialogListener;
    }

    public CustomVersionDialogListener getCustomVersionDialogListener() {
        return customVersionDialogListener;
    }

    public RequestVersionBuilder getRequestVersionBuilder() {
        return requestVersionBuilder;
    }

    public NotificationBuilder getNotificationBuilder() {
        return notificationBuilder;
    }

    public DownloadBuilder setNotificationBuilder(NotificationBuilder notificationBuilder) {
        this.notificationBuilder = notificationBuilder;
        return this;
    }

    public String getApkName() {
        return apkName;
    }

    public boolean isDirectDownload() {
        return isDirectDownload;
    }

    public DownloadBuilder setDirectDownload(boolean directDownload) {
        isDirectDownload = directDownload;
        return this;
    }

    public void executeMission() {
        Context context = UpgradeClient.getInstance().getContext();

        if (apkName == null) {
            apkName = context.getPackageName();
        }

        if (notificationBuilder.getIcon() == 0) {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

                final int appIconResId = applicationInfo.icon;
                notificationBuilder.setIcon(appIconResId);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (checkWhetherNeedRequestVersion()) {
            RequestVersionManager.getInstance().requestVersion(this);

        } else {
            download();
        }
    }

    public void download() {
        VersionService.builder = this;
        VersionService.enqueueWork(UpgradeClient.getInstance().getContext());
    }

    private boolean checkWhetherNeedRequestVersion() {
        return getRequestVersionBuilder() != null;
    }

}