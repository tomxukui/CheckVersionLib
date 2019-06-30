package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;

public interface CustomDownloadingDialogListener {

    DownloadingDialog getCustomDownloadingDialog(Context context, int progress, UpgradeInfo upgradeInfo);

}