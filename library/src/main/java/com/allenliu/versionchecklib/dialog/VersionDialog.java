package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;

public interface VersionDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void setOnConfirmListener(DialogInterface.OnClickListener listener);

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

}
