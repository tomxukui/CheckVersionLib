package com.allenliu.versionchecklib.builder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.callback.OnCancelListener;
import com.allenliu.versionchecklib.callback.OnCustomDialogListener;
import com.allenliu.versionchecklib.callback.OnDownloadListener;
import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.http.RequestVersionManager;
import com.allenliu.versionchecklib.service.VersionService;
import com.allenliu.versionchecklib.utils.UpgradeUtil;

import java.io.File;

public class DownloadBuilder {

    private RequestVersionBuilder mRequestVersionBuilder;
    private NotificationBuilder mNotificationBuilder;

    private OnCustomDialogListener mOnCustomDialogListener;
    private OnCancelListener mOnCancelListener;
    private OnDownloadListener mOnDownloadListener;

    private UpgradeInfo mUpgradeInfo;//版本更新信息
    private String mApkDir;//存储apk的文件目录
    private String mApkName;//apk的文件名称

    private boolean isSilentDownload;

    private boolean isForceRedownload;
    private boolean isShowDownloadingDialog;
    private boolean isShowNotification;
    private boolean isShowDownloadFailDialog;
    private boolean isDirectDownload;

    private DownloadBuilder(@Nullable RequestVersionBuilder requestVersionBuilder, @Nullable UpgradeInfo upgradeInfo) {
        mRequestVersionBuilder = requestVersionBuilder;
        mUpgradeInfo = upgradeInfo;

        initialize();
    }

    public DownloadBuilder(@NonNull RequestVersionBuilder requestVersionBuilder) {
        this(requestVersionBuilder, null);
    }

    public DownloadBuilder(@NonNull UpgradeInfo upgradeInfo) {
        this(null, upgradeInfo);
    }

    private void initialize() {
        isSilentDownload = false;
        isForceRedownload = true;
        isShowDownloadingDialog = true;
        isShowNotification = true;
        isDirectDownload = false;
        isShowDownloadFailDialog = true;
    }

    /***********************************监听事件***************************************/

    //自定义对话框的回调
    public OnCustomDialogListener getOnCustomDialogListener() {
        return mOnCustomDialogListener;
    }

    public DownloadBuilder setOnCustomDialogListener(@Nullable OnCustomDialogListener listener) {
        mOnCustomDialogListener = listener;
        return this;
    }

    //取消更新的回调
    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    public DownloadBuilder setOnCancelListener(OnCancelListener cancelListener) {
        mOnCancelListener = cancelListener;
        return this;
    }

    //下载apk的回调
    public OnDownloadListener getOnDownloadListener() {
        return mOnDownloadListener;
    }

    public DownloadBuilder setOnDownloadListener(@Nullable OnDownloadListener listener) {
        mOnDownloadListener = listener;
        return this;
    }

    /***********************************变量***************************************/

    //版本更新信息
    public UpgradeInfo getUpgradeInfo() {
        return mUpgradeInfo;
    }

    public DownloadBuilder setUpgradeInfo(@NonNull UpgradeInfo upgradeInfo) {
        mUpgradeInfo = upgradeInfo;
        return this;
    }

    //存储apk的文件目录
    public String getApkDir() {
        return mApkDir;
    }

    public DownloadBuilder setApkDir(String apkDir) {
        mApkDir = apkDir;
        return this;
    }

    //获取apk的文件名称
    public String getApkName() {
        return mApkName;
    }

    //获取apk的文件地址
    public File getApkFile() {
        return new File(mApkDir, mApkName);
    }

    public DownloadBuilder setSilentDownload(boolean silentDownload) {
        isSilentDownload = silentDownload;
        return this;
    }

    public DownloadBuilder setForceRedownload(boolean forceRedownload) {
        isForceRedownload = forceRedownload;
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

    public boolean isSilentDownload() {
        return isSilentDownload;
    }


    public boolean isForceRedownload() {
        return isForceRedownload;
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


    public RequestVersionBuilder getRequestVersionBuilder() {
        return mRequestVersionBuilder;
    }

    public NotificationBuilder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    public DownloadBuilder setNotificationBuilder(@NonNull NotificationBuilder notificationBuilder) {
        mNotificationBuilder = notificationBuilder;
        return this;
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

        if (TextUtils.isEmpty(mApkDir)) {
            mApkDir = UpgradeUtil.getDefaultApkDir();
        }

        if (mNotificationBuilder == null) {
            mNotificationBuilder = NotificationBuilder.create();
        }
        if (mNotificationBuilder.getIcon() == 0) {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

                final int appIconResId = applicationInfo.icon;
                mNotificationBuilder.setIcon(appIconResId);

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
        if (mUpgradeInfo == null) {
            return;
        }

        String downloadUrl = mUpgradeInfo.getDownloadUrl();

        if (TextUtils.isEmpty(downloadUrl)) {
            return;
        }

        mApkName = UpgradeUtil.getApkName(downloadUrl);

        if (mApkName == null) {
            return;
        }

        VersionService.builder = this;
        VersionService.enqueueWork();
    }

    private boolean checkWhetherNeedRequestVersion() {
        return getRequestVersionBuilder() != null;
    }

}