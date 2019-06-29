package com.allenliu.versionchecklib.callback;

import java.io.File;

/**
 * Created by allenliu on 2017/8/16.
 */

public interface ApkDownloadListener {
    void onDownloading(int progress);

    void onDownloadSuccess(File file);

    void onDownloadFail();
}
