package com.allenliu.versionchecklib.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.dialog.VersionDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultDownloadFailedDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultDownloadingDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultVersionDialog;
import com.allenliu.versionchecklib.event.DownloadingProgressEvent;
import com.allenliu.versionchecklib.event.UpgradeEvent;
import com.allenliu.versionchecklib.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadFailedListener;
import com.allenliu.versionchecklib.v2.callback.CustomDownloadingDialogListener;
import com.allenliu.versionchecklib.v2.callback.CustomVersionDialogListener;
import com.allenliu.versionchecklib.service.VersionService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaskDialogActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static final String EXTRA_DIALOG_TYPE = "EXTRA_TYPE";
    private static final String TYPE_VERSION = "TYPE_VERSION";
    private static final String TYPE_DOWNLOADING = "TYPE_DOWNLOADING";
    private static final String TYPE_DOWNLOAD_FAILED = "TYPE_DOWNLOAD_FAILED";

    private VersionDialog mVersionDialog;
    private DownloadingDialog mDownloadingDialog;
    private DownloadFailedDialog mDownloadFailedDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        dismissVersionDialog();
        dismissDownloadingDialog();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadingProgressEvent(DownloadingProgressEvent event) {
        if (mDownloadingDialog != null && mDownloadingDialog.isShowing()) {
            mDownloadingDialog.showProgress(event.progress);
        }
    }

    private void handleIntent(Intent intent) {
        if (isFinishing()) {
            return;
        }

        String dialogType = intent.getStringExtra(EXTRA_DIALOG_TYPE);
        if (dialogType == null) {
            dialogType = "";
        }

        switch (dialogType) {

            case TYPE_VERSION: {
                showVersionDialog();
                dismissDownloadingDialog();
                dismissDownloadFailedDialog();
            }
            break;

            case TYPE_DOWNLOADING: {
                dismissVersionDialog();
                showDownloadingDialog();
                dismissDownloadFailedDialog();
            }
            break;

            case TYPE_DOWNLOAD_FAILED: {
                dismissVersionDialog();
                dismissDownloadingDialog();
                showDownloadFailedDialog();
            }
            break;

            default:
                break;

        }
    }

    /**
     * 显示版本信息对话框
     */
    private void showVersionDialog() {
        if (mVersionDialog == null) {
            UIData data = VersionService.builder.getVersionBundle();
            CustomVersionDialogListener customVersionDialogListener = VersionService.builder.getCustomVersionDialogListener();

            if (customVersionDialogListener != null) {
                mVersionDialog = customVersionDialogListener.getCustomVersionDialog(this, data);

            } else {
                mVersionDialog = new DefaultVersionDialog.Builder(this)
                        .setTitle(data.getTitle())
                        .setMessage(data.getContent())
                        .force(data.getForce())
                        .create();
            }
            mVersionDialog.setOnDismissListener(this);
            mVersionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_UPGRADE));
                }

            });
            mVersionDialog.setOnConfirmListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CONFIRM_UPGRADE));
                }

            });
        }

        if (!mVersionDialog.isShowing()) {
            mVersionDialog.show();
        }
    }

    /**
     * 隐藏版本信息对话框
     */
    private void dismissVersionDialog() {
        if (mVersionDialog != null) {
            if (mVersionDialog.isShowing()) {
                mVersionDialog.dismiss();
            }

            mVersionDialog = null;
        }
    }

    /**
     * 显示下载对话框
     */
    private void showDownloadingDialog() {
        if (mDownloadingDialog == null) {
            UIData data = VersionService.builder.getVersionBundle();
            CustomDownloadingDialogListener customDownloadingDialogListener = VersionService.builder.getCustomDownloadingDialogListener();

            if (customDownloadingDialogListener != null) {
                mDownloadingDialog = customDownloadingDialogListener.getCustomDownloadingDialog(this, 0, data);

            } else {
                mDownloadingDialog = new DefaultDownloadingDialog.Builder(this).create();
            }
            mDownloadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_DOWNLOADING));
                }

            });
            mDownloadingDialog.setOnInstallListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.DOWNLOAD_COMPLETE));
                }

            });
        }

        if (!mDownloadingDialog.isShowing()) {
            mDownloadingDialog.show();
        }
    }

    /**
     * 隐藏下载对话框
     */
    private void dismissDownloadingDialog() {
        if (mDownloadingDialog != null) {
            if (mDownloadingDialog.isShowing()) {
                mDownloadingDialog.dismiss();
            }

            mDownloadingDialog = null;
        }
    }

    /**
     * 显示下载失败对话框
     */
    private void showDownloadFailedDialog() {
        if (mDownloadFailedDialog == null) {
            UIData data = VersionService.builder.getVersionBundle();
            CustomDownloadFailedListener customDownloadFailedListener = VersionService.builder.getCustomDownloadFailedListener();

            if (customDownloadFailedListener != null) {
                mDownloadFailedDialog = customDownloadFailedListener.getCustomDownloadFailed(this, data);

            } else {
                mDownloadFailedDialog = new DefaultDownloadFailedDialog.Builder(this).create();
            }
            mDownloadFailedDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.CANCEL_RETRY_DOWNLOAD));
                }

            });
            mDownloadFailedDialog.setOnConfirmListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventBus.getDefault().post(new UpgradeEvent(UpgradeEvent.RETRY_DOWNLOAD));
                }

            });
        }

        if (!mDownloadFailedDialog.isShowing()) {
            mDownloadFailedDialog.show();
        }
    }

    /**
     * 隐藏下载失败对话框
     */
    private void dismissDownloadFailedDialog() {
        if (mDownloadFailedDialog != null) {
            if (mDownloadFailedDialog.isShowing()) {
                mDownloadFailedDialog.dismiss();
            }

            mDownloadFailedDialog = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if ((mVersionDialog == null || !mVersionDialog.isShowing()) && (mDownloadingDialog == null || !mDownloadingDialog.isShowing()) && (mDownloadFailedDialog == null || !mDownloadFailedDialog.isShowing())) {
            finish();
        }
    }

    public static class Builder {

        private Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, MaskDialogActivity.class);
        }

        private Builder setDialogType(String dialogType) {
            mIntent.putExtra(EXTRA_DIALOG_TYPE, dialogType);
            return this;
        }

        public Builder setVersionType() {
            return setDialogType(TYPE_VERSION);
        }

        public Builder setDownloadingType() {
            return setDialogType(TYPE_DOWNLOADING);
        }

        public Builder setDownloadFailedType() {
            return setDialogType(TYPE_DOWNLOAD_FAILED);
        }

        public Intent create() {
            return mIntent;
        }

    }

}