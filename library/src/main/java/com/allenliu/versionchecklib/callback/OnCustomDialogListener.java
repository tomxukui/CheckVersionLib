package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.dialog.VersionDialog;

public interface OnCustomDialogListener {

    VersionDialog getVersionDialog(Context context, UpgradeInfo upgradeInfo);

    DownloadingDialog getDownloadingDialog(Context context, UpgradeInfo upgradeInfo);

    DownloadFailedDialog getDownloadFailedDialog(Context context, UpgradeInfo upgradeInfo);

}