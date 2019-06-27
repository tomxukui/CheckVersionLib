package com.allenliu.versionchecklib.v2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.allenliu.versionchecklib.core.http.AllenHttp;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.ui.VersionService;

/**
 * Created by allenliu on 2018/1/12.
 */

public class AllenVersionChecker {

    private Context mContext;

    private AllenVersionChecker() {
    }

    public static AllenVersionChecker getInstance() {
        return AllenVersionCheckerHolder.allenVersionChecker;
    }

    private static class AllenVersionCheckerHolder {
        public static final AllenVersionChecker allenVersionChecker = new AllenVersionChecker();
    }

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
    public DownloadBuilder downloadOnly(@Nullable UIData versionBundle) {
        return new DownloadBuilder(null, versionBundle);
    }

    /**
     * 取消所有请求
     */
    public void cancelAllMission() {
        AllenHttp.getHttpClient().dispatcher().cancelAll();
        Intent intent = new Intent(mContext, VersionService.class);
        VersionService.builder = null;
        mContext.stopService(intent);
    }

}