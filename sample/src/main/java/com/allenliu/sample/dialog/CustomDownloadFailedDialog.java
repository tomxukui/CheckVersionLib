package com.allenliu.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.allenliu.sample.R;
import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;

public class CustomDownloadFailedDialog extends Dialog implements DownloadFailedDialog {

    private TextView tv_cancel;
    private TextView tv_retry;

    private UpgradeInfo mUpgradeInfo;

    public CustomDownloadFailedDialog(@NonNull Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.BaseDialog);
        mUpgradeInfo = upgradeInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_download_failed);

        tv_cancel = findViewById(R.id.tv_cancel);
        tv_retry = findViewById(R.id.tv_retry);

        tv_cancel.setOnClickListener(v -> cancel());
    }

    @Override
    public void setOnConfirmListener(View.OnClickListener listener) {
        tv_retry.setOnClickListener(listener);
    }

}
