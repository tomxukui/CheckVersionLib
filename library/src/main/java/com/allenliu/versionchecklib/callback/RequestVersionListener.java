package com.allenliu.versionchecklib.callback;

import android.support.annotation.Nullable;

import com.allenliu.versionchecklib.bean.UpgradeInfo;

public interface RequestVersionListener {

    @Nullable
    UpgradeInfo onRequestVersionSuccess(String result);

    void onRequestVersionFailure(String message);

}