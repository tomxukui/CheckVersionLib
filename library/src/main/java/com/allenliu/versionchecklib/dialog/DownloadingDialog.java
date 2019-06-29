package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;

public interface DownloadingDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

    void setOnDismissListener(DialogInterface.OnDismissListener listener);

    void showProgress(int progress);

    void setOnInstallListener(DialogInterface.OnClickListener listener);

}