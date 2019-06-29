package com.allenliu.versionchecklib.v2.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.dialog.impl.DefaultDownloadingDialog;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;

public class DownloadingActivity extends AllenBaseActivity {

    private DownloadingDialog mDownloadingDialog;

    private int currentProgress = 0;
    protected boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
    }

    public void cancelDownloading(boolean isDownloadCompleted) {
        if (!isDownloadCompleted) {
            AllenHttp.getHttpClient().dispatcher().cancelAll();
            cancelHandler();
            checkForceUpdate();
        }
        finish();
    }

    @Override
    public void receiveEvent(CommonEvent commonEvent) {
        switch (commonEvent.getEventType()) {

            case AllenEventType.UPDATE_DOWNLOADING_PROGRESS: {
                int progress = (int) commonEvent.getData();
                currentProgress = progress;
                updateProgress();
            }
            break;

            case AllenEventType.DOWNLOAD_COMPLETE: {
                cancelDownloading(true);
            }
            break;

            case AllenEventType.CLOSE_DOWNLOADING_ACTIVITY: {
                destroy();
            }
            break;

            default:
                break;

        }
    }

    @Override
    public void showDefaultDialog() {

    }

    @Override
    public void showCustomDialog() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyWithOutDismiss();
        isDestroy = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDestroy = false;
        if (mDownloadingDialog != null && !mDownloadingDialog.isShowing()) {
            mDownloadingDialog.show();
        }
    }

    private void destroyWithOutDismiss() {
        if (mDownloadingDialog != null && mDownloadingDialog.isShowing()) {
            mDownloadingDialog.dismiss();
        }
    }

    private void destroy() {
        if (mDownloadingDialog != null && mDownloadingDialog.isShowing()) {
            mDownloadingDialog.dismiss();
        }
        finish();
    }

    private void updateProgress() {
        if (!isDestroy) {
            if (mDownloadingDialog != null) {
                mDownloadingDialog.showProgress(currentProgress);
            }
        }
    }

    private void showLoadingDialog() {
        if (!isDestroy) {
            if (getVersionBuilder() != null && getVersionBuilder().getCustomDownloadingDialogListener() != null) {
                mDownloadingDialog = getVersionBuilder().getCustomDownloadingDialogListener().getCustomDownloadingDialog(this, currentProgress, getVersionBuilder().getVersionBundle());

            } else {
                mDownloadingDialog = new DefaultDownloadingDialog.Builder(this).create();
            }

            if (mDownloadingDialog != null) {
                mDownloadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelDownloading(false);
                    }

                });
                mDownloadingDialog.show();
            }
        }
    }

}