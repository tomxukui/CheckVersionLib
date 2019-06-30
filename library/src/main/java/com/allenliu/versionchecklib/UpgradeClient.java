package com.allenliu.versionchecklib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.http.HttpClient;
import com.allenliu.versionchecklib.builder.DownloadBuilder;
import com.allenliu.versionchecklib.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.service.VersionService;

public class UpgradeClient {

    private Context mContext;

    private UpgradeClient() {
    }

    public static UpgradeClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final UpgradeClient INSTANCE = new UpgradeClient();
    }

    /**
     * 建议在application中调用
     */
    public void init(Application context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 请求更新接口
     */
    public RequestVersionBuilder requestVersion() {
        return new RequestVersionBuilder();
    }

    /**
     * 只下载请求
     */
    public DownloadBuilder downloadOnly(@Nullable UpgradeInfo upgradeInfo) {
        return new DownloadBuilder(null, upgradeInfo);
    }

    /**
     * 取消所有请求
     */
    public void cancelAllMission() {
        HttpClient.getHttpClient().dispatcher().cancelAll();
        Intent intent = new Intent(mContext, VersionService.class);
        VersionService.builder = null;
        mContext.stopService(intent);
    }

}