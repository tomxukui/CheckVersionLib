package com.allenliu.versionchecklib.builder;

import com.allenliu.versionchecklib.callback.RequestVersionListener;
import com.allenliu.versionchecklib.http.HttpHeaders;
import com.allenliu.versionchecklib.http.HttpParams;
import com.allenliu.versionchecklib.http.HttpRequestMethod;

public class RequestVersionBuilder {

    private HttpRequestMethod mRequestMethod;
    private HttpParams mRequestParams;
    private String mRequestUrl;
    private HttpHeaders mHttpHeaders;
    private RequestVersionListener mRequestVersionListener;

    public RequestVersionBuilder() {
        mRequestMethod = HttpRequestMethod.GET;
    }

    public HttpRequestMethod getRequestMethod() {
        return mRequestMethod;
    }

    public RequestVersionBuilder setRequestMethod(HttpRequestMethod requestMethod) {
        mRequestMethod = requestMethod;
        return this;
    }

    public HttpParams getRequestParams() {
        return mRequestParams;
    }

    public RequestVersionBuilder setRequestParams(HttpParams requestParams) {
        mRequestParams = requestParams;
        return this;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public RequestVersionBuilder setRequestUrl(String requestUrl) {
        mRequestUrl = requestUrl;
        return this;
    }

    public HttpHeaders getHttpHeaders() {
        return mHttpHeaders;
    }

    public RequestVersionBuilder setHttpHeaders(HttpHeaders httpHeaders) {
        mHttpHeaders = httpHeaders;
        return this;
    }

    public RequestVersionListener getRequestVersionListener() {
        return mRequestVersionListener;
    }

    public DownloadBuilder request(RequestVersionListener requestVersionListener) {
        mRequestVersionListener = requestVersionListener;
        return new DownloadBuilder(this, null);
    }

}