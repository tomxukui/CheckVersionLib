package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomDownloadFailedListener {

    DownloadFailedDialog getCustomDownloadFailed(Context context, UpgradeInfo upgradeInfo);

}