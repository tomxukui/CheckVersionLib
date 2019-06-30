package com.allenliu.sample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.allenliu.sample.dialog.CustomDownloadFailedDialog;
import com.allenliu.sample.dialog.CustomDownloadingDialog;
import com.allenliu.sample.dialog.CustomVersionDialog;
import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.builder.DownloadBuilder;
import com.allenliu.versionchecklib.builder.NotificationBuilder;
import com.allenliu.versionchecklib.callback.OnCustomDialogListener;
import com.allenliu.versionchecklib.callback.OnRequestVersionListener;
import com.allenliu.versionchecklib.dialog.DownloadFailedDialog;
import com.allenliu.versionchecklib.dialog.DownloadingDialog;
import com.allenliu.versionchecklib.dialog.VersionDialog;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_version;
    private RadioGroup rg_downloading;
    private RadioGroup rg_download_failed;

    private EditText etAddress;
    private CheckBox forceUpdateCheckBox;
    private CheckBox silentDownloadCheckBox;
    private CheckBox silentDownloadCheckBoxAndInstall;

    private CheckBox forceDownloadCheckBox;
    private CheckBox onlyDownloadCheckBox;
    private CheckBox showNotificationCheckBox;
    private CheckBox showDownloadingCheckBox;
    private CheckBox customNotificationCheckBox;
    private CheckBox showDownloadFailedCheckBox;
    private DownloadBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        rg_version = findViewById(R.id.rg_version);
        rg_downloading = findViewById(R.id.rg_downloading);
        rg_download_failed = findViewById(R.id.rg_download_failed);

        etAddress = findViewById(R.id.etAddress);

        silentDownloadCheckBox = findViewById(R.id.checkbox2);
        forceUpdateCheckBox = findViewById(R.id.checkbox);
        forceDownloadCheckBox = findViewById(R.id.checkbox3);
        onlyDownloadCheckBox = findViewById(R.id.checkbox4);
        showNotificationCheckBox = findViewById(R.id.checkbox5);
        showDownloadingCheckBox = findViewById(R.id.checkbox6);
        customNotificationCheckBox = findViewById(R.id.checkbox7);
        showDownloadFailedCheckBox = findViewById(R.id.checkbox8);
        silentDownloadCheckBoxAndInstall = findViewById(R.id.checkbox20);
    }

    public void v2Click(View view) {
        switch (view.getId()) {
            case R.id.sendbtn:
                sendRequest();
                break;
            case R.id.cancelBtn:
                UpgradeClient.getInstance().cancelAllMission();
                break;
        }
    }

    private void sendRequest() {
        if (onlyDownloadCheckBox.isChecked()) {
            builder = UpgradeClient
                    .getInstance()
                    .downloadOnly(createUpgradeInfo());
        } else {
            builder = UpgradeClient
                    .getInstance()
                    .requestVersion()
                    .setRequestUrl("https://www.baidu.com")
                    .request(new OnRequestVersionListener() {

                        @Override
                        public UpgradeInfo onRequestVersionSuccess(String result) {
                            Toast.makeText(MainActivity.this, "request successful", Toast.LENGTH_SHORT).show();
                            return createUpgradeInfo();
                        }

                        @Override
                        public void onRequestVersionFailure(String message) {
                            Toast.makeText(MainActivity.this, "request failed", Toast.LENGTH_SHORT).show();
                        }

                    });
        }
        if (silentDownloadCheckBox.isChecked())
            builder.setSilentDownload(true);
        if (forceDownloadCheckBox.isChecked())
            builder.setForceRedownload(true);
        if (!showNotificationCheckBox.isChecked())
            builder.setShowNotification(false);
        if (customNotificationCheckBox.isChecked())
            builder.setNotificationBuilder(createCustomNotification());
        if (silentDownloadCheckBoxAndInstall.isChecked()) {
            builder.setShowNotification(false);
        }

        builder.setOnCancelListener(info -> {
            Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();

            if (info.isForce()) {
                finish();
            }
        });

        builder.setOnCustomDialogListener(new OnCustomDialogListener() {

            //更新界面选择
            @Override
            public VersionDialog getVersionDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_version.getCheckedRadioButtonId()) {

                    case R.id.rb_default_version:
                        return null;

                    case R.id.rb_custom_version:
                        return new CustomVersionDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

            //下载进度界面选择
            @Override
            public DownloadingDialog getDownloadingDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_downloading.getCheckedRadioButtonId()) {

                    case R.id.rb_default_downloading:
                        return null;

                    case R.id.rb_custom_downloading:
                        return new CustomDownloadingDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

            @Override
            public DownloadFailedDialog getDownloadFailedDialog(Context context, UpgradeInfo upgradeInfo) {
                switch (rg_download_failed.getCheckedRadioButtonId()) {

                    case R.id.btn_default_download_failed:
                        return null;

                    case R.id.btn_custom_download_failed:
                        return new CustomDownloadFailedDialog(context, upgradeInfo);

                    default:
                        return null;

                }
            }

        });

        //自定义下载路径
        builder.setApkDir(getExternalFilesDir("AllenVersionPath2").getAbsolutePath());
        String address = etAddress.getText().toString();
        if (!TextUtils.isEmpty(address)) {
            builder.setApkDir(address);
        }
        builder.setOnCancelListener(info -> {
            Toast.makeText(MainActivity.this, "Cancel Hanlde", Toast.LENGTH_SHORT).show();

            if (info.isForce()) {
                finish();
            }
        });
        builder.executeMission();
    }

    private NotificationBuilder createCustomNotification() {
        return NotificationBuilder.create()
                .setRingtone(true)
                .setIcon(R.mipmap.dialog4)
                .setTicker("custom_ticker")
                .setContentTitle("custom title")
                .setContentText(getString(R.string.custom_content_text));
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UpgradeInfo createUpgradeInfo() {
        UpgradeInfo data = new UpgradeInfo();
        data.setTitle("更新提示");
        data.setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk");
        data.setContent(getString(R.string.updatecontent));
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpgradeClient.getInstance().cancelAllMission();
    }

}
