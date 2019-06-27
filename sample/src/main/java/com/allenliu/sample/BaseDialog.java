package com.allenliu.sample;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by Allen Liu on 2017/2/23.
 */
public class BaseDialog extends Dialog {

    public BaseDialog(Context context, int theme, int res) {
        super(context, theme);
        setContentView(res);
        setCanceledOnTouchOutside(false);
    }

}