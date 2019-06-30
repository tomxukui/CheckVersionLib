package com.allenliu.sample;

import android.app.Application;

import com.allenliu.versionchecklib.AllenVersionChecker;

public class Mapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AllenVersionChecker.getInstance().init(this);
    }

}