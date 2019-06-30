package com.allenliu.versionchecklib.v2.callback;

import android.content.Context;

import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.builder.UIData;

public interface CustomDownloadingDialogListener {

    DownloadingDialog getCustomDownloadingDialog(Context context, int progress, UIData versionBundle);

}