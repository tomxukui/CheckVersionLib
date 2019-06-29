package com.allenliu.versionchecklib.dialog.impl;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.dialog.VersionDialog;

public class DefaultVersionDialog extends Dialog implements VersionDialog {

    private TextView tv_title;
    private TextView tv_message;
    private TextView tv_cancel;
    private TextView tv_confirm;

    private String mTitle;
    private String mMessage;
    private boolean mForce;

    private OnClickListener mConfirmClickListener;

    public DefaultVersionDialog(@NonNull Context context) {
        super(context, R.style.versionCheckLib_BaseDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default_version);
        initView();
        setView();
    }

    private void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_message = findViewById(R.id.tv_message);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    private void setView() {
        setCanceledOnTouchOutside(false);
        setCancelable(!mForce);

        tv_title.setText(mTitle);

        tv_message.setText(mMessage);
        tv_message.setMovementMethod(ScrollingMovementMethod.getInstance());

        tv_cancel.setText(mForce ? "关闭" : "下次再说");
        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel();
            }

        });

        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onClick(DefaultVersionDialog.this, tv_confirm.getId());
                }
            }

        });
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setForce(boolean force) {
        mForce = force;
    }

    @Override
    public void setOnConfirmListener(OnClickListener listener) {
        mConfirmClickListener = listener;
    }

    public static class Builder {

        private DefaultVersionDialog mDialog;

        public Builder(Context context) {
            mDialog = new DefaultVersionDialog(context);
        }

        public Builder setTitle(String title) {
            mDialog.setTitle(title);
            return this;
        }

        public Builder setMessage(String message) {
            mDialog.setMessage(message);
            return this;
        }

        public Builder force(boolean force) {
            mDialog.setForce(force);
            return this;
        }

        public DefaultVersionDialog create() {
            return mDialog;
        }

    }

}