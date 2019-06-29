package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;

public interface DownloadingDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void showProgress(int progress);

    void setOnInstallListener(DialogInterface.OnClickListener listener);

    void setOnDismissListener(DialogInterface.OnDismissListener listener);

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

}