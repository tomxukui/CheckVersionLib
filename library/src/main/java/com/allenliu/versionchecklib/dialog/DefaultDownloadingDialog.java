package com.allenliu.versionchecklib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.ResouceUtil;
import com.allenliu.versionchecklib.v2.ui.VersionService;

public class DefaultDownloadingDialog extends Dialog {

    private ContentLoadingProgressBar bar_progress;
    private TextView tv_progress;

    private int mCurrentProgress;

    public DefaultDownloadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_default_downloading);

        initView();
        setView();
    }

    private void initView() {
        bar_progress = findViewById(R.id.bar_progress);
        tv_progress = findViewById(R.id.tv_progress);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);
        if (VersionService.builder != null && VersionService.builder.getForceUpdateListener() != null) {
            setCancelable(false);

        } else {
            setCancelable(true);
        }

        setProgressView();
    }

    private void setProgressView() {
        if (bar_progress != null) {
            bar_progress.setProgress(mCurrentProgress);
        }
        if (tv_progress != null) {
            tv_progress.setText(String.format(ResouceUtil.getString(R.string.versionchecklib_progress), mCurrentProgress));
        }
    }

    private void setProgress(int progress) {
        mCurrentProgress = progress;
    }

    public void showProgress(int progress) {
        if (!isShowing()) {
            show();
        }

        setProgress(progress);
        setProgressView();
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public static class Builder {

        private DefaultDownloadingDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultDownloadingDialog(context);
        }

        public Builder setProgress(int progress) {
            mDialog.setProgress(progress);
            return this;
        }

        public DefaultDownloadingDialog create() {
            return mDialog;
        }

    }

}