package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;

public interface CustomDownloadFailedListener {

    DownloadFailedDialog getCustomDownloadFailed(Context context, UpgradeInfo upgradeInfo);

}