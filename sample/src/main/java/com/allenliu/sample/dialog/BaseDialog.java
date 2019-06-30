package com.allenliu.sample.dialog;

import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {

    public BaseDialog(Context context, int theme, int res) {
        super(context, theme);
        setContentView(res);
        setCanceledOnTouchOutside(false);
    }

}