package com.allenliu.versionchecklib.callback;

import java.io.File;

public interface DownloadListener {
    void onCheckerStartDownload();

    void onCheckerDownloading(int progress);

    void onCheckerDownloadSuccess(File file);

    void onCheckerDownloadFail();
}