package com.allenliu.versionchecklib.core;

import android.os.Bundle;

import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;
import com.allenliu.versionchecklib.v2.eventbus.CommonEvent;
import com.allenliu.versionchecklib.v2.ui.AllenBaseActivity;

import org.greenrobot.eventbus.EventBus;

public class PermissionDialogActivity extends AllenBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(true);
    }

    @Override
    public void showDefaultDialog() {
    }

    @Override
    public void showCustomDialog() {
    }

    private void sendBroadcast(boolean result) {
        CommonEvent commonEvent = new CommonEvent();
        commonEvent.setEventType(AllenEventType.REQUEST_PERMISSION);
        commonEvent.setSuccessful(true);
        commonEvent.setData(result);
        EventBus.getDefault().post(commonEvent);

        finish();
    }

}