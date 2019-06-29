package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;

public interface DownloadFailedDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

    void setOnDismissListener(DialogInterface.OnDismissListener listener);

    void setOnConfirmListener(DialogInterface.OnClickListener listener);

}
