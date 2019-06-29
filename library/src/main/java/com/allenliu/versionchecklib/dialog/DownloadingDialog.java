package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;

public interface DownloadingDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void showProgress(int progress);

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

}