package com.allenliu.sample.dialog;

import android.content.Context;

import com.allenliu.versionchecklib.dialog.VersionDialog;

public class CustomVersionDialog extends BaseDialog implements VersionDialog {

    public CustomVersionDialog(Context context) {
        super(context, theme, res);
    }

    @Override
    public void setOnConfirmListener(OnClickListener listener) {

    }

}
