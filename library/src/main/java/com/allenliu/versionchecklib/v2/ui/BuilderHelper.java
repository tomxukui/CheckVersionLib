package com.allenliu.versionchecklib.v2.ui;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.UpgradeUtil;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;

import java.io.File;

public class BuilderHelper {

    private DownloadBuilder builder;

    public BuilderHelper(DownloadBuilder builder) {
        this.builder = builder;
    }

    /**
     * 验证安装包是否存在，并且在安装成功情况下删除安装包
     */
    public void checkAndDeleteAPK() {
        //判断versioncode与当前版本不一样的apk是否存在，存在删除安装包
        try {
            File file = new File(builder.getDownloadAPKPath(), UpgradeUtil.getString(R.string.versionchecklib_download_apkname, UpgradeUtil.getPackageName()));

            if (!UpgradeUtil.checkApkExist(file.getAbsolutePath())) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkForceUpdate() {
        if (builder.getForceUpdateListener() != null) {
            builder.getForceUpdateListener().onShouldForceUpdate();
        }
    }

}
