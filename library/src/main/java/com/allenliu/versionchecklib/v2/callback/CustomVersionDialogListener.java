package com.allenliu.versionchecklib.v2.callback;

import android.content.Context;

import com.allenliu.versionchecklib.dialog.VersionDialog;
import com.allenliu.versionchecklib.v2.builder.UIData;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomVersionDialogListener {
    VersionDialog getCustomVersionDialog(Context context, UIData versionBundle);
}
