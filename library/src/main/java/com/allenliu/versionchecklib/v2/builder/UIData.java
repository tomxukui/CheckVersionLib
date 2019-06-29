package com.allenliu.versionchecklib.v2.builder;

import android.os.Bundle;

/**
 * Created by allenliu on 2018/1/18.
 */

public class UIData {

    private final String TITLE = "title";
    private final String CONTENT = "content";
    private final String DOWNLOAD_URL = "download_url";
    private final String FORCE = "force";

    private Bundle versionBundle;

    public static UIData create() {
        return new UIData();
    }

    private UIData() {
        versionBundle = new Bundle();
        versionBundle.putString(TITLE, "更新提示");
        versionBundle.putString(CONTENT, "检测到新版本");
        versionBundle.putBoolean(FORCE, false);
    }

    public UIData setTitle(String title) {
        versionBundle.putString(TITLE, title);
        return this;
    }

    public UIData setContent(String content) {
        versionBundle.putString(CONTENT, content);
        return this;
    }

    public UIData setDownloadUrl(String downloadUrl) {
        versionBundle.putString(DOWNLOAD_URL, downloadUrl);
        return this;
    }

    public UIData force(boolean force) {
        versionBundle.putBoolean(FORCE, force);
        return this;
    }

    public String getTitle() {
        return versionBundle.getString(TITLE);
    }

    public String getContent() {
        return versionBundle.getString(CONTENT);
    }

    public String getDownloadUrl() {
        return versionBundle.getString(DOWNLOAD_URL);
    }

    public boolean getForce() {
        return versionBundle.getBoolean(FORCE);
    }

    public Bundle getVersionBundle() {
        return versionBundle;
    }

}
