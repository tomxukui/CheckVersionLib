package com.allenliu.versionchecklib.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.utils.ResouceUtil;
import com.allenliu.versionchecklib.v2.ui.VersionService;

public class DefaultDownloadingDialog extends Dialog implements DownloadingDialog {

    private ContentLoadingProgressBar bar_progress;
    private TextView tv_progress;

    public DefaultDownloadingDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
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
    }

    @Override
    public void showProgress(int progress) {
        if (!isShowing()) {
            show();
        }

        if (bar_progress != null) {
            bar_progress.setProgress(progress);
        }
        if (tv_progress != null) {
            tv_progress.setText(String.format(ResouceUtil.getString(R.string.versionchecklib_progress), progress));
        }
    }

    public static class Builder {

        private DefaultDownloadingDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultDownloadingDialog(context);
        }

        public DefaultDownloadingDialog create() {
            return mDialog;
        }

    }

}