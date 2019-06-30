package com.allenliu.sample.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.TextView;

import com.allenliu.sample.R;
import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.VersionDialog;

public class CustomVersionDialog extends Dialog implements VersionDialog {

    private TextView tv_title;
    private TextView tv_msg;
    private Button btn_commit;

    private UpgradeInfo mUpgradeInfo;
    private OnClickListener mOnConfirmListener;

    public CustomVersionDialog(@NonNull Context context, UpgradeInfo upgradeInfo) {
        super(context, R.style.BaseDialog);
        mUpgradeInfo = upgradeInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_version);

        tv_title = findViewById(R.id.tv_title);
        tv_msg = findViewById(R.id.tv_msg);
        btn_commit = findViewById(R.id.btn_commit);

        tv_title.setText(mUpgradeInfo.getTitle());
        tv_msg.setText(mUpgradeInfo.getContent());
        btn_commit.setOnClickListener(v -> {
            if (mOnConfirmListener != null) {
                mOnConfirmListener.onClick(CustomVersionDialog.this, btn_commit.getId());
            }
        });
    }

    @Override
    public void setOnConfirmListener(OnClickListener listener) {
        mOnConfirmListener = listener;
    }

}