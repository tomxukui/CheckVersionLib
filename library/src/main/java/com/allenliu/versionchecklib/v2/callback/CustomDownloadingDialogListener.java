package com.allenliu.versionchecklib.v2.callback;

import android.app.Dialog;
import android.content.Context;

import com.allenliu.versionchecklib.v2.builder.UIData;

public interface CustomDownloadingDialogListener {

    Dialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle);

    void updateUI(Dialog dialog, int progress, UIData versionBundle);

}