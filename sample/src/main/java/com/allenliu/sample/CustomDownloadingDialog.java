package com.allenliu.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allenliu.versionchecklib.dialog.DownloadingDialog;

public class CustomDownloadingDialog extends Dialog implements DownloadingDialog {

    private TextView tv_progress;
    private ProgressBar progressBar;

    public CustomDownloadingDialog(@NonNull Context context) {
        super(context, R.style.BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_download_layout);
        setCanceledOnTouchOutside(false);

        tv_progress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.pb);
    }

    @Override
    public void showProgress(int progress) {
        progressBar.setProgress(progress);
        tv_progress.setText(String.format("%d/100", progress));
    }

}