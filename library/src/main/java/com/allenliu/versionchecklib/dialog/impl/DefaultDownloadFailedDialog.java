package com.allenliu.versionchecklib.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;

public class DefaultDownloadFailedDialog extends Dialog implements DownloadFailedDialog {

    private TextView tv_cancel;
    private TextView tv_confirm;

    private OnClickListener mOnConfirmClickListener;

    public DefaultDownloadFailedDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default_download_failed);
        initView();
        setView();
    }

    private void initView() {
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);

        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }

        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onClick(DefaultDownloadFailedDialog.this, tv_confirm.getId());
                }
            }

        });
    }

    @Override
    public void setOnConfirmListener(OnClickListener listener) {
        mOnConfirmClickListener = listener;
    }

    public static class Builder {

        private DefaultDownloadFailedDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultDownloadFailedDialog(context);
        }

        public DefaultDownloadFailedDialog create() {
            return mDialog;
        }

    }

}