package com.allenliu.versionchecklib.callback;

import com.allenliu.versionchecklib.bean.UpgradeInfo;

public interface OnRequestVersionListener {

    UpgradeInfo onRequestVersionSuccess(String result);

    void onRequestVersionFailure(String message);

}