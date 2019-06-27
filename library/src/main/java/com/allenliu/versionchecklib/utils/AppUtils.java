package com.allenliu.versionchecklib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.allenliu.versionchecklib.core.VersionFileProvider;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.callback.CustomInstallListener;

import java.io.File;

public final class AppUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private AppUtils() {
        throw new Error("Do not need instantiate!");
    }

    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri,
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        AllenVersionChecker.getInstance().cancelAllMission();
    }

    public static void installApk(Context context, File file, CustomInstallListener listener) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = VersionFileProvider.getUriForFile(context, context.getPackageName() + ".versionProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        if (listener != null) {
            listener.install(context, uri);
        } else {
            intent.setDataAndType(uri,
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
            AllenVersionChecker.getInstance().cancelAllMission();
        }
    }

}