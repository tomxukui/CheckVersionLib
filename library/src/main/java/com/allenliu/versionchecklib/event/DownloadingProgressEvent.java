package com.allenliu.versionchecklib.event;

public class DownloadingProgressEvent {

    public final int progress;

    public DownloadingProgressEvent(int progress) {
        this.progress = progress;
    }

}
