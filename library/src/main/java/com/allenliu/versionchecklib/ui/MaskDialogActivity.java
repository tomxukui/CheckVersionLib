package com.allenliu.versionchecklib.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.dialog.VersionDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultDownloadingDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultVersionDialog;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;
import com.allenliu.versionchecklib.v2.ui.VersionService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaskDialogActivity extends AppCompatActivity {

    private static final String EXTRA_DIALOG_TYPE = "EXTRA_TYPE";
    private static final String TYPE_VERSION = "TYPE_VERSION";
    private static final String TYPE_DOWNLOADING = "TYPE_DOWNLOADING";

    private VersionDialog mVersionDialog;
    private DownloadingDialog mDownloadingDialog;

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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(CommonEvent commonEvent) {
    }

    private void handleIntent(Intent intent) {
        String dialogType = intent.getStringExtra(EXTRA_DIALOG_TYPE);
        if (dialogType == null) {
            dialogType = "";
        }

        switch (dialogType) {

            case TYPE_VERSION: {
                showVersionDialog();
                dismissDownloadingDialog();
            }
            break;

            case TYPE_DOWNLOADING: {
                dismissVersionDialog();
                showDownloadingDialog();
            }
            break;

            default: {
                dismissVersionDialog();
                dismissDownloadingDialog();
            }
            break;

        }
    }

    /**
     * 显示版本信息对话框
     */
    private void showVersionDialog() {
        if (mVersionDialog == null) {
            UIData data = VersionService.builder.getVersionBundle();

            if (VersionService.builder != null && VersionService.builder.getCustomVersionDialogListener() != null) {
                mVersionDialog = VersionService.builder
                        .getCustomVersionDialogListener()
                        .getCustomVersionDialog(this, VersionService.builder.getVersionBundle());

            } else {
                mVersionDialog = new DefaultVersionDialog.Builder(this)
                        .setTitle(data.getTitle())
                        .setMessage(data.getContent())
                        .force(data.getForce())
                        .create();
            }
            mVersionDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                }

            });
            mVersionDialog.setOnConfirmListener(new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
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
            if (VersionService.builder != null && VersionService.builder.getCustomDownloadingDialogListener() != null) {
                mDownloadingDialog = VersionService.builder.getCustomDownloadingDialogListener().getCustomDownloadingDialog(this, 0, VersionService.builder.getVersionBundle());

            } else {
                mDownloadingDialog = new DefaultDownloadingDialog.Builder(this).create();
            }

            mDownloadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    cancelDownloading(false);
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
     * 取消下载
     */
    private void cancelDownloading(boolean isDownloadCompleted) {
        if (!isDownloadCompleted) {
            AllenHttp.getHttpClient().dispatcher().cancelAll();
            callbackOnCancel();
            checkForceUpdate();
        }
        finish();
    }

    /**
     * 回调取消
     */
    protected void callbackOnCancel() {
        if (VersionService.builder != null && VersionService.builder.getOnCancelListener() != null) {
            VersionService.builder.getOnCancelListener().onCancel();
        }
    }

    protected void checkForceUpdate() {
//        if (VersionService.builder != null && VersionService.builder.getForceUpdateListener() != null) {
//            VersionService.builder.getForceUpdateListener().onShouldForceUpdate();
//            finish();
//        }
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

        public Intent create() {
            return mIntent;
        }

    }

}