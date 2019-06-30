package com.allenliu.versionchecklib.callback;

import android.support.annotation.Nullable;

import com.allenliu.versionchecklib.bean.UpgradeInfo;

/**
 * Created by allenliu on 2018/1/12.
 */

public interface RequestVersionListener {
    /**
     * @param result the result string of request
     * @return developer should return version bundle ,to use when showing UI page,could be null
     */
    @Nullable
    UpgradeInfo onRequestVersionSuccess(String result);

    void onRequestVersionFailure(String message);

}
