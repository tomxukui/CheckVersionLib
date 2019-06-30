package com.allenliu.sample;

import android.app.Application;

import com.allenliu.versionchecklib.UpgradeClient;

public class Mapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        UpgradeClient.getInstance().init(this);
    }

}