package com.allenliu.versionchecklib.callback;

import com.allenliu.versionchecklib.bean.UpgradeInfo;

public interface OnCancelListener {

    void onCancel(UpgradeInfo info);

}