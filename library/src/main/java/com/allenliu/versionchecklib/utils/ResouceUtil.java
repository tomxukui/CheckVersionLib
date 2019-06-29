package com.allenliu.versionchecklib.utils;

import android.support.annotation.StringRes;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;

public class ResouceUtil {

    public static String getString(@StringRes int resId) {
        return AllenVersionChecker.getInstance().getContext().getString(resId);
    }

}
