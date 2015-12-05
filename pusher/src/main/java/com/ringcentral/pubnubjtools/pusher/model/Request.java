package com.ringcentral.pubnubjtools.pusher.model;

import java.util.Map;

public class Request implements Describable {

    private final Map<String, String> headers;
    private final String url;

    public Request(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
        return toString();
    }

    @Override
    public String getInfoDescription() {
        return url;
    }

}
