package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.dialog.VersionDialog;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomVersionDialogListener {
    VersionDialog getCustomVersionDialog(Context context, UpgradeInfo upgradeInfo);
}
