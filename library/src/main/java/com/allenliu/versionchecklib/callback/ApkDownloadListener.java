package com.allenliu.versionchecklib.callback;

import java.io.File;

public interface ApkDownloadListener {

    void onDownloading(int progress);

    void onDownloadSuccess(File file);

    void onDownloadFail();

}