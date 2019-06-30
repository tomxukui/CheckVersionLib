package com.allenliu.versionchecklib.callback;

import android.content.Context;

import com.allenliu.versionchecklib.dialog.VersionDialog;
import com.allenliu.versionchecklib.builder.UIData;

/**
 * Created by allenliu on 2018/1/18.
 */

public interface CustomVersionDialogListener {
    VersionDialog getCustomVersionDialog(Context context, UIData versionBundle);
}
