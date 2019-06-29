package com.allenliu.versionchecklib.v2.ui;

import android.content.Context;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.net.DownloadMangerV2;

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
            Context context = AllenVersionChecker.getInstance().getContext();

            File file = new File(builder.getDownloadAPKPath(), context.getString(R.string.versionchecklib_download_apkname, context.getPackageName()));

            if (!DownloadMangerV2.checkAPKIsExists(context, file.getAbsolutePath())) {
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
