package com.allenliu.sample;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.bean.UpgradeInfo;
import com.allenliu.versionchecklib.builder.DownloadBuilder;
import com.allenliu.versionchecklib.builder.NotificationBuilder;
import com.allenliu.versionchecklib.callback.RequestVersionListener;

public class MainActivity extends AppCompatActivity {

    private EditText etAddress;
    private RadioGroup radioGroup;
    private CheckBox forceUpdateCheckBox;
    private CheckBox silentDownloadCheckBox;
    private CheckBox silentDownloadCheckBoxAndInstall;

    private CheckBox forceDownloadCheckBox;
    private CheckBox onlyDownloadCheckBox;
    private CheckBox showNotificationCheckBox;
    private CheckBox showDownloadingCheckBox;
    private CheckBox customNotificationCheckBox;
    private CheckBox showDownloadFailedCheckBox;
    private RadioGroup radioGroup2, radioGroup3;
    private DownloadBuilder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        etAddress = findViewById(R.id.etAddress);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        radioGroup3 = findViewById(R.id.radioGroup3);

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
                    .request(new RequestVersionListener() {
                        @Nullable
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
        if (!showDownloadingCheckBox.isChecked())
            builder.setShowDownloadingDialog(false);
        if (!showNotificationCheckBox.isChecked())
            builder.setShowNotification(false);
        if (customNotificationCheckBox.isChecked())
            builder.setNotificationBuilder(createCustomNotification());
        if (!showDownloadFailedCheckBox.isChecked())
            builder.setShowDownloadFailDialog(false);
        if (silentDownloadCheckBoxAndInstall.isChecked()) {
            builder.setDirectDownload(true);
            builder.setShowNotification(false);
            builder.setShowDownloadingDialog(false);
            builder.setShowDownloadFailDialog(false);
        }

        builder.setOnCancelListener(info -> {
            Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();

            if (info.isForce()) {
                finish();
            }
        });

        //更新界面选择
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn1:
                break;
            case R.id.btn2:
                break;
            case R.id.btn3:
                break;
        }

        //下载进度界面选择
        switch (radioGroup2.getCheckedRadioButtonId()) {
            case R.id.btn21:
                break;
            case R.id.btn22:
                break;
        }
        //下载失败界面选择
        switch (radioGroup3.getCheckedRadioButtonId()) {
            case R.id.btn31:
                break;
            case R.id.btn32:
                break;
        }
        //自定义下载路径
        builder.setDownloadAPKPath(getExternalFilesDir("AllenVersionPath2").getAbsolutePath() + "/");
        String address = etAddress.getText().toString();
        if (address != null && !"".equals(address)) {
            builder.setDownloadAPKPath(address);
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
        data.setTitle(getString(R.string.update_title));
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
