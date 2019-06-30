package com.allenliu.versionchecklib.http;

import android.os.Handler;
import android.os.Looper;

import com.allenliu.versionchecklib.UpgradeClient;
import com.allenliu.versionchecklib.builder.DownloadBuilder;
import com.allenliu.versionchecklib.builder.RequestVersionBuilder;
import com.allenliu.versionchecklib.builder.UIData;
import com.allenliu.versionchecklib.callback.RequestVersionListener;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author AllenLiu
 * @version 1.0
 * @date 2019/4/30
 * @since 1.0
 */
public class RequestVersionManager {

    private RequestVersionManager() {
    }

    public static RequestVersionManager getInstance() {
        return Holder.instance;
    }

    public static class Holder {
        static RequestVersionManager instance = new RequestVersionManager();
    }

    /**
     * 请求版本接口
     */
    public void requestVersion(final DownloadBuilder builder) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
                RequestVersionBuilder requestVersionBuilder = builder.getRequestVersionBuilder();
                OkHttpClient client = HttpClient.getHttpClient();
                HttpRequestMethod requestMethod = requestVersionBuilder.getRequestMethod();
                Request request = null;

                switch (requestMethod) {

                    case GET: {
                        request = HttpClient.get(requestVersionBuilder).build();
                    }
                    break;

                    case POST: {
                        request = HttpClient.post(requestVersionBuilder).build();
                    }
                    break;

                    case POSTJSON: {
                        request = HttpClient.postJson(requestVersionBuilder).build();
                    }
                    break;

                    default:
                        break;

                }

                final RequestVersionListener requestVersionListener = requestVersionBuilder.getRequestVersionListener();
                Handler handler = new Handler(Looper.getMainLooper());
                if (requestVersionListener != null) {
                    try {
                        final Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            final String result = response.body() != null ? response.body().string() : null;

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    UIData versionBundle = requestVersionListener.onRequestVersionSuccess(result);
                                    if (versionBundle != null) {
                                        builder.setVersionBundle(versionBundle);
                                        builder.download();
                                    }
                                }
                            });

                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    requestVersionListener.onRequestVersionFailure(response.message());
                                    UpgradeClient.getInstance().cancelAllMission();
                                }
                            });
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                requestVersionListener.onRequestVersionFailure(e.getMessage());
                                UpgradeClient.getInstance().cancelAllMission();
                            }
                        });

                    }

                } else {
                    throw new RuntimeException("using request version function,you must set a requestVersionListener");
                }
            }
        });

    }

}
