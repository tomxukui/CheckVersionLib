package com.allenliu.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.widget.Button;

import com.allenliu.sample.R;
import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;

public class CustomDownloadingDialog extends Dialog implements DownloadingDialog {

    private ContentLoadingProgressBar bar_progress;
    private Button btn_install;

    private UpgradeInfo mUpgradeInfo;
    private OnClickListener mOnInstallListener;

    public CustomDownloadingDialog(@NonNull Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.BaseDialog);
        mUpgradeInfo = upgradeInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_downloading);

        bar_progress = findViewById(R.id.bar_progress);
        btn_install = findViewById(R.id.btn_install);

        btn_install.setOnClickListener(v -> {
            if (mOnInstallListener != null) {
                mOnInstallListener.onClick(CustomDownloadingDialog.this, btn_install.getId());
            }
        });
    }

    @Override
    public void showProgress(int progress) {
        bar_progress.setProgress(progress);
    }

    @Override
    public void setOnInstallListener(OnClickListener listener) {
        mOnInstallListener = listener;
    }

}