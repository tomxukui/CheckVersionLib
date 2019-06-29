package com.allenliu.versionchecklib.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.utils.ResouceUtil;
import com.allenliu.versionchecklib.v2.ui.VersionService;

public class DefaultDownloadingDialog extends Dialog implements DownloadingDialog {

    private ContentLoadingProgressBar bar_progress;
    private TextView tv_status;
    private TextView tv_progress;
    private TextView tv_install;

    public DefaultDownloadingDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
        setContentView(R.layout.dialog_default_downloading);

        initView();
        setView();
    }

    private void initView() {
        bar_progress = findViewById(R.id.bar_progress);
        tv_status = findViewById(R.id.tv_status);
        tv_progress = findViewById(R.id.tv_progress);
        tv_install = findViewById(R.id.tv_install);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);
        if (VersionService.builder != null && VersionService.builder.getForceUpdateListener() != null) {
            setCancelable(false);

        } else {
            setCancelable(true);
        }

        tv_install.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });
    }

    @Override
    public void showProgress(int progress) {
        if (!isShowing()) {
            show();
        }

        if (bar_progress != null) {
            bar_progress.setProgress(progress);
        }
        if (tv_status != null) {
            tv_status.setText(progress < 100 ? R.string.upgrade_downloading : R.string.upgrade_download_complete);
        }
        if (tv_progress != null) {
            tv_progress.setText(String.format(ResouceUtil.getString(R.string.versionchecklib_progress), progress));
        }
        if (tv_install != null) {
            tv_install.setVisibility(progress < 100 ? View.GONE : View.VISIBLE);
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