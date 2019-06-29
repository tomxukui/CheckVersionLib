package com.allenliu.versionchecklib.event;

public class UpgradeEvent {

    public static final String CONFIRM_UPGRADE = "CONFIRM_UPGRADE";
    public static final String CANCEL_UPGRADE = "CANCEL_UPGRADE";

//    public static final String START_DOWNLOADING = "START_DOWNLOADING";
    public static final String CANCEL_DOWNLOADING = "CANCEL_DOWNLOADING";

    public final String type;

    public UpgradeEvent(String type) {
        this.type = type;
    }

}