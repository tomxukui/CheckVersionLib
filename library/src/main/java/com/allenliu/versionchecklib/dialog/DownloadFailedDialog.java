package com.allenliu.versionchecklib.dialog;

import android.content.DialogInterface;
import android.view.View;

public interface DownloadFailedDialog {

    void show();

    void dismiss();

    boolean isShowing();

    void setOnCancelListener(DialogInterface.OnCancelListener listener);

    void setOnDismissListener(DialogInterface.OnDismissListener listener);

    void setOnConfirmListener(View.OnClickListener listener);

}
