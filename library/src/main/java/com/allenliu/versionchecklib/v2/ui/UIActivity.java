package com.allenliu.versionchecklib.v2.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.allenliu.versionchecklib.R;
import com.allenliu.versionchecklib.utils.AllenEventBusUtil;
import com.allenliu.versionchecklib.utils.AppUtils;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.eventbus.AllenEventType;

import java.io.File;

public class UIActivity extends AllenBaseActivity implements DialogInterface.OnCancelListener {

    private Dialog versionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showVersionDialog();
    }

    @Override
    public void showDefaultDialog() {
        if (getVersionBuilder() != null) {
            UIData uiData = getVersionBuilder().getVersionBundle();
            String title = "提示";
            String content = "检测到新版本";

            if (uiData != null) {
                title = uiData.getTitle();
                content = uiData.getContent();
            }

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(content)
                    .setPositiveButton(getString(R.string.versionchecklib_confirm), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dealVersionDialogCommit();
                        }

                    });

            if (getVersionBuilder().getForceUpdateListener() == null) {
                alertBuilder.setNegativeButton(getString(R.string.versionchecklib_cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancel(versionDialog);
                    }

                });
            }
            alertBuilder.setCancelable(false);

            versionDialog = alertBuilder.create();
            versionDialog.setCanceledOnTouchOutside(false);
            versionDialog.show();
        }
    }

    @Override
    public void showCustomDialog() {
        if (getVersionBuilder() != null) {
//            versionDialog = getVersionBuilder()
//                    .getCustomVersionDialogListener()
//                    .getCustomVersionDialog(this, getVersionBuilder().getVersionBundle());

            versionDialog.show();

            try {
                //自定义dialog，commit button 必须存在
                final View view = versionDialog.findViewById(R.id.versionchecklib_version_dialog_commit);
                if (view != null) {
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            dealVersionDialogCommit();
                        }

                    });

                } else {
                    throwWrongIdsException();
                }

                //如果有取消按钮，id也必须对应
                View cancelView = versionDialog.findViewById(R.id.versionchecklib_version_dialog_cancel);
                if (cancelView != null) {
                    cancelView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            onCancel(versionDialog);
                        }

                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                throwWrongIdsException();
            }
        }
    }

    private void showVersionDialog() {
        if (getVersionBuilder() != null && getVersionBuilder().getCustomVersionDialogListener() != null) {
            showCustomDialog();

        } else {
            showDefaultDialog();
        }

        if (versionDialog != null) {
            versionDialog.setOnCancelListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (versionDialog != null && versionDialog.isShowing()) {
            versionDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (versionDialog != null && !versionDialog.isShowing()) {
            versionDialog.show();
        }
    }

    private void dealVersionDialogCommit() {
        DownloadBuilder versionBuilder = getVersionBuilder();
        if (versionBuilder != null) {
            //如果是静默下载直接安装
            if (versionBuilder.isSilentDownload()) {
                File downloadFile = new File(versionBuilder.getDownloadAPKPath(), getString(R.string.versionchecklib_download_apkname, versionBuilder.getApkName() != null ? versionBuilder.getApkName() : getPackageName()));
                AppUtils.installApk(this, downloadFile, versionBuilder.getCustomInstallListener());
                checkForceUpdate();

            } else {
                AllenEventBusUtil.sendEventBus(AllenEventType.START_DOWNLOAD_APK);
            }

            finish();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        cancelHandler();
        checkForceUpdate();
        AllenVersionChecker.getInstance().cancelAllMission();
        finish();
    }

}